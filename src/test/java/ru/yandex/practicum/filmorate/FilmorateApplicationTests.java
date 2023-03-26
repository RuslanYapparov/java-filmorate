package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.SpringApplication;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;

@SpringBootTest
class FilmorateApplicationTests {
	private static final HttpClient client = HttpClient.newHttpClient();
	private static final String URL_START = "http://localhost:8080/";
	private static final ObjectMapper jackson = new ObjectMapper();
	private static final HttpResponse.BodyHandler<String> BODY_HANDLER = HttpResponse.BodyHandlers.ofString();
	private static HttpRequest request;
	private static HttpResponse<String> response;
	private static HttpRequest.BodyPublisher bodyPublisher;
	private static User user;
	private static Film film;

	@Test
	void contextLoads() {
	}

	@BeforeAll
	static void initialize() {
		SpringApplication.run(FilmorateApplication.class);
		jackson.registerModule(new JavaTimeModule());
		jackson.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	// Также пытался завершить работу приложения в методе с аннтоцией @AfterAll, но пришлось отказаться из-за ошибок,
	// Которые не смог исправить. В IDEA работа завершается после тестов сама с exitcode 0. Решил, что это приемлемо.

	@BeforeEach
	void makeValidUserAndFilmAndPostThemToCleanStorages() throws IOException, InterruptedException {
		user = User.builder()
				.id(0)
				.email("sexmaster96@gmail.com")
				.login("tecktonick_killer")
				.name("Владимир")
				.birthday(LocalDate.of(1996, 12, 12))
				.build();
		film = Film.builder()
				.id(0)
				.name("Whores & whales")
				.description("Adventures of women in whales world")
				.releaseDate(LocalDate.of(1996, 12, 12))
				.duration(127)
				.build();

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
	}

	@Test
	void shouldHaveStoragesWithOneElementBeforeEachTests() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		User[] usersFromServer = jackson.readValue(response.body(), User[].class);
		assertEquals(1, usersFromServer.length);
		assertEquals(User.builder().id(usersFromServer[0].getId()).email("sexmaster96@gmail.com")
				.login("tecktonick_killer").name("Владимир")
				.birthday(LocalDate.of(1996, 12, 12)).build(), usersFromServer[0]);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		Film[] filmsFromServer = jackson.readValue(response.body(), Film[].class);
		assertEquals(1, filmsFromServer.length);
		assertEquals(Film.builder().id(filmsFromServer[0].getId()).name("Whores & whales")
				.description("Adventures of women in whales world")
				.releaseDate(LocalDate.of(1996, 12, 12)).duration(127).build(), filmsFromServer[0]);
	}

	@Test
	void shouldNotPostUserWithInvalidId() throws IOException, InterruptedException {
		user = user.toBuilder().id(-1).build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithDuplicatedEmail() throws IOException, InterruptedException {
		user = user.toBuilder().login("tapochek").name("Роман")
				.birthday(LocalDate.of(1985, 12,21)).build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidEmailFirstVariant() throws IOException, InterruptedException {
		user = user.toBuilder().email("sosiska").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidEmailSecondVariant() throws IOException, InterruptedException {
		user = user.toBuilder().email("tanec v nochi@mail.ru").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidEmailThirdVariant() throws IOException, InterruptedException {
		user = user.toBuilder().email("football_fan@ mail.ru").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidEmailFourthVariant() throws IOException, InterruptedException {
		user = user.toBuilder().email("@mail.ru").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidEmailFifthVariant() throws IOException, InterruptedException {
		user = user.toBuilder().email("sosiska@yandex").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidEmailSixthVariant() throws IOException, InterruptedException {
		user = user.toBuilder().email(" ").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidEmailSeventhVariant() throws IOException, InterruptedException {
		user = user.toBuilder().email("\r\t").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithNullEmail() throws IOException, InterruptedException {
		user = user.toBuilder().email(null).build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidLoginFirstVariant() throws IOException, InterruptedException {
		user = user.toBuilder().login("").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidLoginSecondVariant() throws IOException, InterruptedException {
		user = user.toBuilder().login("  ").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidLoginThirdVariant() throws IOException, InterruptedException {
		user = user.toBuilder().login("lapa kota").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidLoginFourthVariant() throws IOException, InterruptedException {
		user = user.toBuilder().login("\n").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithNullLogin() throws IOException, InterruptedException {
		user = user.toBuilder().login(null).build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithInvalidBirthday() throws IOException, InterruptedException {
		user = user.toBuilder().birthday(LocalDate.of(2025,8,7)).build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostUserWithNullBirthday() throws IOException, InterruptedException {
		user = user.toBuilder().birthday(null).build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldPostUserWithNullName() throws IOException, InterruptedException {
		user = user.toBuilder().email("martyshka@kdfnr.com").name(null).build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
	}

	@Test
	void shouldPostUserWithBlankName() throws IOException, InterruptedException {
		user = user.toBuilder().name(" ").email("bisexual_bus@rbk.v").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithInvalidId() throws IOException, InterruptedException {
		film = film.toBuilder().id(-1).build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithInvalidNameFirstVariant() throws IOException, InterruptedException {
		film = film.toBuilder().name("").build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithInvalidNameSecondVariant() throws IOException, InterruptedException {
		film = film.toBuilder().name(" ").build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithInvalidNameThirdVariant() throws IOException, InterruptedException {
		film = film.toBuilder().name("\n").build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithNullName() throws IOException, InterruptedException {
		film = film.toBuilder().name(null).build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithInvalidDescription() throws IOException, InterruptedException {
		film = film.toBuilder().description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithNullDescription() throws IOException, InterruptedException {
		film = film.toBuilder().description(null).build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithInvalidReleaseDate() throws IOException, InterruptedException {
		film = film.toBuilder().releaseDate(LocalDate.of(1850, 9, 27)).build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithReleaseDateInFuture() throws IOException, InterruptedException {
		film = film.toBuilder().releaseDate(LocalDate.of(2025, 9, 27)).build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithNullReleaseDate() throws IOException, InterruptedException {
		film = film.toBuilder().releaseDate(null).build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithNegativeDuration() throws IOException, InterruptedException {
		film = film.toBuilder().duration(-1).build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPostFilmWithNullDuration() throws IOException, InterruptedException {
		film = film.toBuilder().duration(0).build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldReturnListsOfSavedUsers() throws IOException, InterruptedException {
		user = user.toBuilder().email("kuzkin_otec@yandex.ru").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		User[] usersFromServer = jackson.readValue(response.body(), User[].class);
		assertEquals(2, usersFromServer.length);
	}

	@Test
	void shouldReturnListsOfSavedFilms() throws IOException, InterruptedException {
		film = film.toBuilder().name("Приключения Ашота").build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
					.uri(URI.create(URL_START + "films"))
					.POST(bodyPublisher)
					.version(HttpClient.Version.HTTP_1_1)
					.header("Content-type", "application/json")
					.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
					.uri(URI.create(URL_START + "films"))
					.GET()
					.version(HttpClient.Version.HTTP_1_1)
					.header("Content-type", "application/json")
					.build();
		response = client.send(request, BODY_HANDLER);
		Film[] filmsFromServer = jackson.readValue(response.body(), Film[].class);
		assertEquals(2, filmsFromServer.length);
	}

	@Test
	void shouldReturnSavedUserById() throws IOException, InterruptedException {
		user = user.toBuilder().email("sladkayakoshechka1953@list.ru").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		User userFromServer = jackson.readValue(response.body(), User.class);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		userFromServer = jackson.readValue(response.body(), User.class);
		assertEquals(user.toBuilder().id(userFromServer.getId()).build(), userFromServer);
	}

	@Test
	void shouldReturnSavedFilmById() throws IOException, InterruptedException {
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		Film filmFromServer = jackson.readValue(response.body(), Film.class);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + filmFromServer.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		filmFromServer = jackson.readValue(response.body(), Film.class);
		assertEquals(film.toBuilder().id(filmFromServer.getId()).build(), filmFromServer);
	}

	@Test
	void shouldUpdateValidUser() throws IOException, InterruptedException {
		user = user.toBuilder().email("vagonchikSerundoy123@ui.ru").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		User userFromServer = jackson.readValue(response.body(), User.class);

		user = user.toBuilder().id(userFromServer.getId()).build();
		userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
	}

	@Test
	void shouldUpdateValidFilm() throws IOException, InterruptedException {
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		Film filmFromServer = jackson.readValue(response.body(), Film.class);

		film = film.toBuilder().id(filmFromServer.getId()).build();
		filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
	}

	@Test
	void shouldDeleteUserById() throws IOException, InterruptedException {
		user = user.toBuilder().email("178@kj.se").build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		User userFromServer = jackson.readValue(response.body(), User.class);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		userFromServer = jackson.readValue(response.body(), User.class);
		assertEquals(user.toBuilder().id(userFromServer.getId()).build(), userFromServer);
	}

	@Test
	void shouldDeleteFilmById() throws IOException, InterruptedException {
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		Film filmFromServer = jackson.readValue(response.body(), Film.class);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + filmFromServer.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		filmFromServer = jackson.readValue(response.body(), Film.class);
		assertEquals(200, response.statusCode());
		assertEquals(film.toBuilder().id(filmFromServer.getId()).build(), filmFromServer);
	}

	@Test
	void shouldNotPutUserWhenIncorrectId() throws IOException, InterruptedException {
		user = user.toBuilder().id(777).build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());
	}

	@Test
	void shouldNotReturnUserWhenIncorrectId() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/identifier1"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotDeleteUserWhenIncorrectId() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/77.77"))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotPutFilmWhenIncorrectId() throws IOException, InterruptedException {
		film = film.toBuilder().id(123).build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());
	}

	@Test
	void shouldNotReturnFilmWhenIncorrectId() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/777"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());
	}

	@Test
	void shouldNotDeleteFilmWhenIncorrectId() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/id_Abcdef"))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

}

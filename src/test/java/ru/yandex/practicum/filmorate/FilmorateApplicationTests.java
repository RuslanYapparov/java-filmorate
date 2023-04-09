package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.SpringApplication;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;
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
				.email("sexmaster96@gmail.com")
				.login("tecktonick_killer")
				.name("Владимир")
				.birthday(LocalDate.of(1996, 12, 12))
				.build();
		film = Film.builder()
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

		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		user = jackson.readValue(response.body(), UserRestCommand.class).convertToDomainObject();

		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		film = jackson.readValue(response.body(), FilmRestCommand.class).convertToDomainObject();
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
		UserRestCommand[] usersFromServer = jackson.readValue(response.body(), UserRestCommand[].class);
		assertEquals(1, usersFromServer.length);
		assertEquals(new UserRestCommand(User.builder().id(usersFromServer[0].getId()).email("sexmaster96@gmail.com")
				.login("tecktonick_killer").name("Владимир")
				.birthday(LocalDate.of(1996, 12, 12)).build()), usersFromServer[0]);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		FilmRestCommand[] filmsFromServer = jackson.readValue(response.body(), FilmRestCommand[].class);
		assertEquals(1, filmsFromServer.length);
		assertEquals(new FilmRestCommand(Film.builder().id(filmsFromServer[0].getId()).name("Whores & whales")
				.description("Adventures of women in whales world")
				.releaseDate(LocalDate.of(1996, 12, 12)).duration(127).build()), filmsFromServer[0]);
	}

	@Test
	void shouldNotPostUserWithInvalidId() throws IOException, InterruptedException {
		user = user.toBuilder().id(-1).build();
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
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
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(409, response.statusCode());
	}

	@ParameterizedTest
	@ValueSource(strings = { "    ", "\r", "\t", "\n", "sosiska", "tanec v nochi@mail.ru", "football_fan@ mail.ru",
			"@mail.ru", "kill:ninjas@ki.st" })
	@NullAndEmptySource
	void shouldNotPostUserWithInvalidEmail(String email) throws IOException, InterruptedException {
		user = user.toBuilder().email(email).build();
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
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
	void shouldNotPostUserWithInvalidEmailWithoutPoint() throws IOException, InterruptedException {
		user = user.toBuilder().email("sosiska@yandex").build();
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
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

	@ParameterizedTest
	@ValueSource(strings = { "  ", "\r", "\t", "\n", " lapa kota" })
	@NullAndEmptySource
	void shouldNotPostUserWithInvalidLoginFirstVariant(String login) throws IOException, InterruptedException {
		user = user.toBuilder().login(login).build();
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
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
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
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
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
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

	@ParameterizedTest
	@ValueSource(strings = { "   ", "\r", "\t", "\n" })
	@NullAndEmptySource
	void shouldPostUserWithNullName(String name) throws IOException, InterruptedException {
		user = user.toBuilder().email("martyshka@kdfnr.com").name(name).build();
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
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
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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

	@ParameterizedTest
	@ValueSource(strings = { "  ", "\t", "\n", "\r"})
	@NullAndEmptySource
	void shouldNotPostFilmWithInvalidName(String name) throws IOException, InterruptedException {
		film = film.toBuilder().name(name).build();
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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
		film = film.toBuilder().description("a".repeat(201)).build();
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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

	@ParameterizedTest
	@ValueSource(ints = { -1, 0 })
	void shouldNotPostFilmWithNegativeOrNullDuration(int duration) throws IOException, InterruptedException {
		film = film.toBuilder().duration(duration).build();
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
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
		UserRestCommand[] usersFromServer = jackson.readValue(response.body(), UserRestCommand[].class);
		assertEquals(2, usersFromServer.length);
	}

	@Test
	void shouldReturnListsOfSavedFilms() throws IOException, InterruptedException {
		film = film.toBuilder().name("Приключения Ашота").build();
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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
		FilmRestCommand[] filmsFromServer = jackson.readValue(response.body(), FilmRestCommand[].class);
		assertEquals(2, filmsFromServer.length);
	}

	@Test
	void shouldReturnSavedUserById() throws IOException, InterruptedException {
		user = user.toBuilder().email("sladkayakoshechka1953@list.ru").build();
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		UserRestCommand userFromServer = jackson.readValue(response.body(), UserRestCommand.class);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		userFromServer = jackson.readValue(response.body(), UserRestCommand.class);
		assertEquals(new UserRestCommand(user.toBuilder().id(userFromServer.getId()).build()), userFromServer);
	}

	@Test
	void shouldReturnSavedFilmById() throws IOException, InterruptedException {
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		FilmRestCommand filmFromServer = jackson.readValue(response.body(), FilmRestCommand.class);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + filmFromServer.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		filmFromServer = jackson.readValue(response.body(), FilmRestCommand.class);
		assertEquals(new FilmRestCommand(film.toBuilder().id(filmFromServer.getId()).build()), filmFromServer);
	}

	@Test
	void shouldUpdateValidUser() throws IOException, InterruptedException {
		user = user.toBuilder().email("vagonchikSerundoy123@ui.ru").build();
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		UserRestCommand userFromServer = jackson.readValue(response.body(), UserRestCommand.class);

		user = user.toBuilder().id(userFromServer.getId()).build();
		userJson = jackson.writeValueAsString(new UserRestCommand(user));
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
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		FilmRestCommand filmFromServer = jackson.readValue(response.body(), FilmRestCommand.class);

		film = film.toBuilder().id(filmFromServer.getId()).build();
		filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		UserRestCommand userFromServer = jackson.readValue(response.body(), UserRestCommand.class);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		userFromServer = jackson.readValue(response.body(), UserRestCommand.class);
		assertEquals(new UserRestCommand(user.toBuilder().id(userFromServer.getId()).build()), userFromServer);
	}

	@Test
	void shouldDeleteFilmById() throws IOException, InterruptedException {
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		FilmRestCommand filmFromServer = jackson.readValue(response.body(), FilmRestCommand.class);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + filmFromServer.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		filmFromServer = jackson.readValue(response.body(), FilmRestCommand.class);
		assertEquals(200, response.statusCode());
		assertEquals(new FilmRestCommand(film.toBuilder().id(filmFromServer.getId()).build()), filmFromServer);
	}

	@Test
	void shouldNotPutUserWhenIncorrectId() throws IOException, InterruptedException {
		user = user.toBuilder().id(777).build();
		String userJson = jackson.writeValueAsString(new UserRestCommand(user));
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

	@ParameterizedTest
	@ValueSource(strings = { "-1", "0", "identifier1", "9999999"})
	void shouldNotReturnUserWhenIncorrectId(String id) throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + id))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		int code = response.statusCode();
		assertTrue(code == 400 || code == 404 || code == 500);
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
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(film));
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

	@Test
	void shouldReturnUserFiendsListById() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		UserRestCommand[] usersFromServer = jackson.readValue(response.body(), UserRestCommand[].class);
		assertEquals(0, usersFromServer.length);
	}

	@Test
	void shouldMakeAndUnmakeFriends() throws IOException, InterruptedException {
		String userJson = jackson.writeValueAsString(new UserRestCommand(user.toBuilder().email("12@rt.y").build()));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		UserRestCommand userFromServer = jackson.readValue(response.body(), UserRestCommand.class);

		bodyPublisher = HttpRequest.BodyPublishers.ofString("");
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/" + userFromServer.getId()))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		UserRestCommand[] usersFromServer = jackson.readValue(response.body(), UserRestCommand[].class);
		assertEquals(1, usersFromServer.length);
		assertEquals(userFromServer.getId(), usersFromServer[0].getId());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer.getId() + "/friends"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		usersFromServer = jackson.readValue(response.body(), UserRestCommand[].class);
		assertEquals(1, usersFromServer.length);
		assertEquals(user.getId(), usersFromServer[0].getId());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/" + userFromServer.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		usersFromServer = jackson.readValue(response.body(), UserRestCommand[].class);
		assertEquals(0, usersFromServer.length);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer.getId() + "/friends"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		usersFromServer = jackson.readValue(response.body(), UserRestCommand[].class);
		assertEquals(0, usersFromServer.length);
	}

	@Test
	void shouldReturnCommonFriendsListWithFriendAfterAddingAndWithoutFriendsAfterRemoving()
			throws IOException, InterruptedException {
		String userJson = jackson.writeValueAsString(new UserRestCommand(user.toBuilder().email("12@rt.y").build()));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		UserRestCommand userFromServer1 = jackson.readValue(response.body(), UserRestCommand.class);

		userJson = jackson.writeValueAsString(new UserRestCommand(user.toBuilder().email("34@rt.y").build()));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		UserRestCommand userFromServer2 = jackson.readValue(response.body(), UserRestCommand.class);

		bodyPublisher = HttpRequest.BodyPublishers.ofString("");
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/" + userFromServer1.getId()))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer2.getId() + "/friends/" + userFromServer1.getId()))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/common/" + userFromServer2.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		UserRestCommand[] usersFromServer = jackson.readValue(response.body(), UserRestCommand[].class);
		assertEquals(1, usersFromServer.length);
		assertEquals(userFromServer1.getId(), usersFromServer[0].getId());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/" + userFromServer1.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/common/" + userFromServer2.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		usersFromServer = jackson.readValue(response.body(), UserRestCommand[].class);
		assertEquals(0, usersFromServer.length);
	}

	@Test
	void shouldAddLikeToFilmAndRemoveLikeFromFilm() throws IOException, InterruptedException {
		bodyPublisher = HttpRequest.BodyPublishers.ofString("");
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + film.getId() + "/like/" + user.getId()))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + film.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		FilmRestCommand filmFromServer = jackson.readValue(response.body(), FilmRestCommand.class);
		assertEquals(1, filmFromServer.convertToDomainObject().getLikes().size());
		assertTrue(filmFromServer.convertToDomainObject().getLikes().contains(user.getId()));

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + film.getId() + "/like/" + user.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + film.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		filmFromServer = jackson.readValue(response.body(), FilmRestCommand.class);
		assertEquals(0, filmFromServer.convertToDomainObject().getLikes().size());
	}

	@Test
	void shouldReturnListOfSortedByNumberOfLikesFilms() throws IOException, InterruptedException {
		String filmJson = jackson.writeValueAsString(new FilmRestCommand(Film.builder().name("Crazy Potato")
				.description("Potato").releaseDate(LocalDate.of(1975, 11,17))
				.duration(85).build()));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		FilmRestCommand anotherFilmFromServer = jackson.readValue(response.body(), FilmRestCommand.class);

		bodyPublisher = HttpRequest.BodyPublishers.ofString("");
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + film.getId() + "/like/" + user.getId()))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		film.getLikes().add(user.getId());
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/popular?count=1"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		FilmRestCommand[] filmsFromServer = jackson.readValue(response.body(), FilmRestCommand[].class);
		assertEquals(1, filmsFromServer.length);
		assertEquals(new FilmRestCommand(film), filmsFromServer[0]);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/popular"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		filmsFromServer = jackson.readValue(response.body(), FilmRestCommand[].class);
		assertEquals(2, filmsFromServer.length);
		assertEquals(new FilmRestCommand(film), filmsFromServer[0]);
		assertEquals(anotherFilmFromServer, filmsFromServer[1]);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/popular?count=foo"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

	@ParameterizedTest
	@ValueSource(strings = { "999" })
	void shouldReturn404CodeWhenGetIncorrectId(String id) throws IOException, InterruptedException {
		bodyPublisher = HttpRequest.BodyPublishers.ofString("");
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/" + id))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + id + "/friends/" + user.getId()))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + id + "/friends"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/" + id))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + id + "/friends/" + user.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/common/" + id))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + id + "/friends/common/" + user.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + id + "/like/" + user.getId()))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + film.getId() + "/like/" + id))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + id + "/like/" + user.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + film.getId() + "/like/" + id))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());
	}

	@ParameterizedTest
	@ValueSource(strings = { "cat" })
	@NullSource
	void shouldReturn400CodeWhenGetNotNumericId(String id) throws IOException, InterruptedException {
		bodyPublisher = HttpRequest.BodyPublishers.ofString("");
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/" + id))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());

		bodyPublisher = HttpRequest.BodyPublishers.ofString("");
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + id + "/friends/" + user.getId()))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + id + "/friends"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());

		bodyPublisher = HttpRequest.BodyPublishers.ofString("");
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/" + id))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());

		bodyPublisher = HttpRequest.BodyPublishers.ofString("");
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + id + "/friends/" + user.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + user.getId() + "/friends/common/" + id))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + id + "/friends/common/" + user.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + id + "/like/" + user.getId()))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + film.getId() + "/like/" + id))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + id + "/like/" + user.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + film.getId() + "/like/" + id))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(400, response.statusCode());
	}

}
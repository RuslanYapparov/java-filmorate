package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.HashSet;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapperImpl;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapperImpl;
import ru.yandex.practicum.filmorate.model.domain.Film;
import ru.yandex.practicum.filmorate.model.domain.Genre;
import ru.yandex.practicum.filmorate.model.domain.RatingMpa;
import ru.yandex.practicum.filmorate.model.domain.User;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.GenreRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.RatingMpaRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.presentation.restview.UserRestView;

@SpringBootTest
class FilmorateApplicationTest {
	private static final HttpClient client = HttpClient.newHttpClient();
	private static final String URL_START = "http://localhost:8080/";
	private static final ObjectMapper jackson = new ObjectMapper();
	private static final HttpResponse.BodyHandler<String> BODY_HANDLER = HttpResponse.BodyHandlers.ofString();
	private static final UserMapper userMapper = new UserMapperImpl();
	private static final FilmMapper filmMapper = new FilmMapperImpl();
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
				.friends(new HashSet<>())
				.build();
		film = Film.builder()
				.name("Whores & whales")
				.description("Adventures of women in whales world")
				.releaseDate(LocalDate.of(1996, 12, 12))
				.duration(127)
				.rate((byte) 2)
				.rating(RatingMpa.R)
				.likes(new HashSet<>())
				.genres(new HashSet<>())
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

		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		user = readUserForTest(response.body());

		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		film = readFilmForTest(response.body());
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
		UserRestView[] usersFromServer = jackson.readValue(response.body(), UserRestView[].class);
		assertEquals(1, usersFromServer.length);
		assertEquals("sexmaster96@gmail.com", usersFromServer[0].getEmail());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		FilmRestView[] filmsFromServer = jackson.readValue(response.body(), FilmRestView[].class);
		assertEquals(1, filmsFromServer.length);
		assertEquals("Whores & whales", filmsFromServer[0].getName());
	}

	@Test
	void shouldNotPostUserWithInvalidId() throws IOException, InterruptedException {
		user = user.toBuilder().id(-1).build();
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		UserRestView[] usersFromServer = jackson.readValue(response.body(), UserRestView[].class);
		assertEquals(2, usersFromServer.length);
	}

	@Test
	void shouldReturnListsOfSavedFilms() throws IOException, InterruptedException {
		film = film.toBuilder().name("Приключения Ашота").build();
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		FilmRestView[] filmsFromServer = jackson.readValue(response.body(), FilmRestView[].class);
		assertEquals(2, filmsFromServer.length);
	}

	@Test
	void shouldReturnSavedUserById() throws IOException, InterruptedException {
		user = user.toBuilder().email("sladkayakoshechka1953@list.ru").build();
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		UserRestView userFromServer = jackson.readValue(response.body(), UserRestView.class);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		userFromServer = jackson.readValue(response.body(), UserRestView.class);
		assertEquals(userMapper.toRestView(user.toBuilder().id(userFromServer.getId()).build()),
				userFromServer);
	}

	@Test
	void shouldReturnSavedFilmById() throws IOException, InterruptedException {
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		FilmRestView filmFromServer = jackson.readValue(response.body(), FilmRestView.class);
		assertEquals(200, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + filmFromServer.getId()))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		filmFromServer = jackson.readValue(response.body(), FilmRestView.class);
		assertEquals(film.getDescription(), filmFromServer.getDescription());
	}

	@Test
	void shouldUpdateValidUser() throws IOException, InterruptedException {
		user = user.toBuilder().email("vagonchikSerundoy123@ui.ru").build();
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		UserRestView userFromServer = jackson.readValue(response.body(), UserRestView.class);

		user = user.toBuilder().id(userFromServer.getId()).build();
		userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		FilmRestView filmFromServer = jackson.readValue(response.body(), FilmRestView.class);

		film = film.toBuilder().id(filmFromServer.getId()).build();
		filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		UserRestView userFromServer = jackson.readValue(response.body(), UserRestView.class);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		userFromServer = jackson.readValue(response.body(), UserRestView.class);
		assertEquals(userMapper.toRestView(user.toBuilder().id(userFromServer.getId()).build()),
				userFromServer);
	}

	@Test
	void shouldDeleteFilmById() throws IOException, InterruptedException {
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		FilmRestView filmFromServer = jackson.readValue(response.body(), FilmRestView.class);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/" + filmFromServer.getId()))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		filmFromServer = jackson.readValue(response.body(), FilmRestView.class);
		assertEquals(200, response.statusCode());
		assertEquals(film.getDescription(), filmFromServer.getDescription());
	}

	@Test
	void shouldNotPutUserWhenIncorrectId() throws IOException, InterruptedException {
		user = user.toBuilder().id(777).build();
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user));
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
		assertTrue(code == 400 || code == 404);
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
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(film));
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
		UserRestView[] usersFromServer = jackson.readValue(response.body(), UserRestView[].class);
		assertEquals(0, usersFromServer.length);
	}

	@Test
	void shouldMakeAndUnmakeFriends() throws IOException, InterruptedException {
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user.toBuilder()
				.email("12@rt.y")
				.build()));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		UserRestView userFromServer = jackson.readValue(response.body(), UserRestView.class);

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
		UserRestView[] usersFromServer = jackson.readValue(response.body(), UserRestView[].class);
		assertEquals(1, usersFromServer.length);
		assertEquals(userFromServer.getId(), usersFromServer[0].getId());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer.getId() + "/friends"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		usersFromServer = jackson.readValue(response.body(), UserRestView[].class);
		assertEquals(0, usersFromServer.length);

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
		usersFromServer = jackson.readValue(response.body(), UserRestView[].class);
		assertEquals(0, usersFromServer.length);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/" + userFromServer.getId() + "/friends"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		usersFromServer = jackson.readValue(response.body(), UserRestView[].class);
		assertEquals(0, usersFromServer.length);
	}

	@Test
	void shouldReturnCommonFriendsListWithFriendAfterAddingAndWithoutFriendsAfterRemoving()
			throws IOException, InterruptedException {
		String userJson = jackson.writeValueAsString(createCommandObjectForTest(user.toBuilder()
				.email("12@rt.y")
				.build()));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		UserRestView userFromServer1 = jackson.readValue(response.body(), UserRestView.class);

		userJson = jackson.writeValueAsString(createCommandObjectForTest(user.toBuilder().email("34@rt.y").build()));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		UserRestView userFromServer2 = jackson.readValue(response.body(), UserRestView.class);

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
		UserRestView[] usersFromServer = jackson.readValue(response.body(), UserRestView[].class);
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
		usersFromServer = jackson.readValue(response.body(), UserRestView[].class);
		assertEquals(1, usersFromServer.length);
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
		FilmRestView filmFromServer = jackson.readValue(response.body(), FilmRestView.class);
		assertEquals(1, filmFromServer.getLikes().size());
		assertTrue(filmFromServer.getLikes().contains(user.getId()));

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
		filmFromServer = jackson.readValue(response.body(), FilmRestView.class);
		assertEquals(0, filmFromServer.getLikes().size());
	}

	@Test
	void shouldReturnListOfSortedByNumberOfLikesFilms() throws IOException, InterruptedException {
		String filmJson = jackson.writeValueAsString(createCommandObjectForTest(Film.builder().name("Crazy Potato")
				.description("Potato").releaseDate(LocalDate.of(1975, 11,17))
				.duration(85).rate((byte) 2).rating(RatingMpa.PG).likes(new HashSet<>()).genres(new HashSet<>())
				.build()));
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.POST(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(200, response.statusCode());
		FilmRestView anotherFilmFromServer = jackson.readValue(response.body(), FilmRestView.class);

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
		FilmRestView[] filmsFromServer = jackson.readValue(response.body(), FilmRestView[].class);
		assertEquals(1, filmsFromServer.length);
		assertEquals(film.getDescription(), filmsFromServer[0].getDescription());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/popular"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		filmsFromServer = jackson.readValue(response.body(), FilmRestView[].class);
		assertEquals(2, filmsFromServer.length);
		assertEquals(film.getDescription(), filmsFromServer[0].getDescription());
		assertEquals(anotherFilmFromServer.getDescription(), filmsFromServer[1].getDescription());

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
	@ValueSource(strings = { "0", "-1", })
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

	private UserRestCommand createCommandObjectForTest(User user) {
		UserRestCommand command = new UserRestCommand();
		command.setId(user.getId());
		command.setEmail(user.getEmail());
		command.setLogin(user.getLogin());
		command.setName(user.getName());
		command.setBirthday(user.getBirthday());
		command.setFriends(user.getFriends());
		return command;
	}

	private FilmRestCommand createCommandObjectForTest(Film film) {
		FilmRestCommand command = FilmRestCommand.builder()
				.id(film.getId())
				.name(film.getName())
				.description(film.getDescription())
				.releaseDate(film.getReleaseDate())
				.duration(film.getDuration())
				.rate(film.getRate())
				.mpa(new RatingMpaRestCommand(film.getRating().getId()))
				.likes(film.getLikes())
				.genres(film.getGenres().stream().
						map(genre -> new GenreRestCommand(genre.getId())).
						collect(Collectors.toSet()))
				.build();
		command.setName(film.getName());
		command.setDescription(film.getDescription());
		command.setReleaseDate(film.getReleaseDate());
		command.setDuration(film.getDuration());
		command.setLikes(film.getLikes());
		return command;
	}

	private User readUserForTest(String userView) throws JsonProcessingException {
		UserRestView view = jackson.readValue(userView, UserRestView.class);
		User user = User.builder()
				.id(view.getId())
				.email(view.getEmail())
				.login(view.getLogin())
				.name(view.getName())
				.birthday(view.getBirthday())
				.friends(view.getFriends())
				.build();
		view.getFriends().forEach(userViewId -> user.getFriends().add(userViewId));
		return user;
	}

	private Film readFilmForTest(String filmView) throws JsonProcessingException {
		FilmRestView view = jackson.readValue(filmView, FilmRestView.class);
		Film film = Film.builder()
				.id(view.getId())
				.name(view.getName())
				.description(view.getDescription())
				.releaseDate(view.getReleaseDate())
				.duration(view.getDuration())
				.rate(view.getRate())
				.rating(RatingMpa.getRatingById(view.getMpa().getId()))
				.likes(view.getLikes())
				.genres(view.getGenres().stream()
						.map(genreRestView -> Genre.getGenreById(genreRestView.getId()))
						.collect(Collectors.toSet()))
				.build();
		view.getLikes().forEach(userViewId -> film.getLikes().add(userViewId));
		return film;
	}

}
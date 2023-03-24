package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
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

/* Отдельные юнит-тесты для UserController и FilmController отсутствуют, так как они тестируются в рамках данного
* end-to-end тестирования всего приложения.
* Использую подход с упорядоченным, а не независимым вызовом тестов, так как необходимо протестировать правильность
* обработки запросов web-приложением. Так удается снизить количество кода и упростить логику тестирования.
* Работа методов составляющих классов и валидация тестируются в отдельных юнит-тестах без сценария */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {
	private static final HttpClient client = HttpClient.newHttpClient();
	private static final String URL_START = "http://localhost:8080/";
	private static final ObjectMapper jackson = new ObjectMapper();
	private static final HttpResponse.BodyHandler<String> BODY_HANDLER = HttpResponse.BodyHandlers.ofString();
	private static final User[] USERS_WITH_INVALID_DATA = {
			    User.builder().id(0).email("sexmaster96@gmail.com").login("tecktonick_killer").name("Владимир")
				        .birthday(LocalDate.of(1996, 12, 12)).build(),
				User.builder().id(-1).email("sexmaster96@gmail.com").login("tecktonick_killer").name("Владимир")
						.birthday(LocalDate.of(1996, 12, 12)).build(),
				User.builder().id(0).login("tecktonick_killer").name("Владимир")
						.birthday(LocalDate.of(1996, 12, 12)).build(),
				User.builder().id(0).email("sexmaster96@gmail.com").name("Владимир")
						.birthday(LocalDate.of(1996, 12, 12)).build(),
				User.builder().id(0).email("sexmaster96@gmail.com").login("tecktonick_killer").name("Владимир")
						.build(),
				User.builder().id(0).email("sosiska").login("tecktonick_killer").name("Владимир")
						.birthday(LocalDate.of(1996, 12, 12)).build(),
				User.builder().id(0).email("sexmaster96@gmail.com").login("tecktonick killer").name("Владимир")
						.birthday(LocalDate.of(1996, 12, 12)).build(),
				User.builder().id(0).email("sexmaster96@gmail.com").login("").name("Владимир")
						.birthday(LocalDate.of(1996, 12, 12)).build(),
				User.builder().id(0).email("sexmaster96@gmail.com").login("tecktonick_killer").name("Владимир")
						.birthday(LocalDate.of(1850, 12, 12)).build(),
				User.builder().id(0).email("sexmaster96@gmail.com").login("tecktonick_killer").name("Владимир")
						.birthday(LocalDate.of(2026, 12, 12)).build()
                };
	private static final Film[] FILMS_WITH_INVALID_DATA = {
			Film.builder().id(-1).name("Whores & whales").description("Adventures of women in whales world")
					.releaseDate(LocalDate.of(1996, 12, 12)).duration(127).build(),
			Film.builder().id(0).description("Adventures of women in whales world")
					.releaseDate(LocalDate.of(1996, 12, 12)).duration(127).build(),
			Film.builder().id(0).name("Whores & whales")
					.releaseDate(LocalDate.of(1996, 12, 12)).duration(127).build(),
			Film.builder().id(0).name("Whores & whales").description("Adventures of women in whales world")
					.duration(127).build(),
			Film.builder().id(0).name("Whores & whales").description("Adventures of women in whales world")
					.releaseDate(LocalDate.of(1996, 12, 12)).build(),
			Film.builder().id(0).name("").description("Adventures of women in whales world")
					.releaseDate(LocalDate.of(1996, 12, 12)).duration(127).build(),
			Film.builder().id(0).name("Whores & whales").description("\r\n\t")
					.releaseDate(LocalDate.of(1996, 12, 12)).duration(127).build(),
			Film.builder().id(0).name("Whores & whales").description("Adventures of women in whales world")
					.releaseDate(LocalDate.of(2026, 12, 12)).duration(127).build(),
			Film.builder().id(0).name("Whores & whales").description("Adventures of women in whales world")
					.releaseDate(LocalDate.of(1890, 12, 12)).duration(127).build(),
			Film.builder().id(0).name("Whores & whales").description("Adventures of women in whales world")
					.releaseDate(LocalDate.of(1996, 12, 12)).duration(-1).build()
	};
	private static HttpRequest request;
	private static HttpResponse<String> response;
	private static HttpRequest.BodyPublisher bodyPublisher;

	@BeforeAll
	static void initialize() {
		SpringApplication.run(FilmorateApplication.class);
		jackson.registerModule(new JavaTimeModule());
		jackson.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	// Также пытался завершить работу приложения в методе с аннтоцией @AfterAll, но пришлось отказаться из-за ошибок,
	// Которые не смог исправить. В IDEA работа завершается после тестов сама с exitcode 0. Решил, что это приемлемо.

	@Order(1)
	@Test
	void contextLoads() {
	}

	@Order(2)
	@Test
	void shouldPostNewValidUser() throws IOException, InterruptedException {
		User user = User.builder()
				.id(0)
				.email("sexmaster96@gmail.com")
				.login("tecktonick_killer")
				.name("Владимир")
				.birthday(LocalDate.of(1996, 12, 12))
				.build();
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
		user = user.toBuilder()
				.id(1)
				.build();
		assertEquals(user, jackson.readValue(response.body(), User.class));
	}

	@Order(3)
	@Test
	void shouldNotPostInvalidUsers() throws IOException, InterruptedException {
		for (User user: USERS_WITH_INVALID_DATA) { // Через stream() сделать не получается из-за необработки исключений
			String userJson = jackson.writeValueAsString(user);
			bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
			request = HttpRequest.newBuilder()
					.uri(URI.create(URL_START + "users"))
					.POST(bodyPublisher)
					.version(HttpClient.Version.HTTP_1_1)
					.header("Content-type", "application/json")
					.build();
			response = client.send(request, BODY_HANDLER);
			assertEquals(400, response.statusCode());   // Тело ответа не проверяю, потому что для невалидных
			                                                     // Юзеров в body возвращается json возникшей ошибки
		}
	}

	@Order(4)
	@Test
	void shouldPostNewValidFilm() throws IOException, InterruptedException {
		Film film = Film.builder()
				.id(0)
				.name("Whores & whales")
				.description("Adventures of women in whales world")
				.releaseDate(LocalDate.of(1996, 12, 12))
				.duration(127)
				.build();
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
		film = film.toBuilder()
				.id(1)
				.build();
		assertEquals(film, jackson.readValue(response.body(), Film.class));
	}

	@Order(5)
	@Test
	void shouldNotPostInvalidFilms() throws IOException, InterruptedException {
		for (Film film: FILMS_WITH_INVALID_DATA) {
			String filmJson = jackson.writeValueAsString(film);
			bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
			request = HttpRequest.newBuilder()
					.uri(URI.create(URL_START + "users"))
					.POST(bodyPublisher)
					.version(HttpClient.Version.HTTP_1_1)
					.header("Content-type", "application/json")
					.build();
			response = client.send(request, BODY_HANDLER);
			assertEquals(400, response.statusCode());
		}
	}

	@Order(6)
	@Test
	void shouldReturnListsOfSavedUsersAndFilms() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		User[] usersFromServer = jackson.readValue(response.body(), User[].class);
		assertEquals(1, usersFromServer.length);
		assertEquals(User.builder().id(1).email("sexmaster96@gmail.com").login("tecktonick_killer").name("Владимир")
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
		assertEquals(Film.builder().id(1).name("Whores & whales").description("Adventures of women in whales world")
			.releaseDate(LocalDate.of(1996, 12, 12)).duration(127).build(), filmsFromServer[0]);
	}

	@Order(7)
	@Test
	void shouldReturnSavedUserAndFilmById() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/id1"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		User user = jackson.readValue(response.body(), User.class);
		assertEquals(User.builder().id(1).email("sexmaster96@gmail.com").login("tecktonick_killer").name("Владимир")
				.birthday(LocalDate.of(1996, 12, 12)).build(), user);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/id1"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		Film film = jackson.readValue(response.body(), Film.class);
		assertEquals(Film.builder().id(1).name("Whores & whales").description("Adventures of women in whales world")
				.releaseDate(LocalDate.of(1996, 12, 12)).duration(127).build(), film);
	}

	@Order(8)
	@Test
	void shouldUpdateValidUserAndFilm() throws IOException, InterruptedException {
		User user = User.builder()
				.id(1)
				.email("sexmaster96@gmail.com")
				.login("narkoman_pavlik")
				.birthday(LocalDate.of(1996, 12, 12))
				.build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		User userFromServer = jackson.readValue(response.body(), User.class);
		assertEquals(200, response.statusCode());
		user = user.toBuilder().name("narkoman_pavlik").build();
		assertEquals(user, userFromServer);

		Film film = Film.builder()
				.id(1)
				.name("Whores & holes")
				.description("Adventures of women in video world")
				.releaseDate(LocalDate.of(1996, 12, 12))
				.duration(125)
				.build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		Film filmFromServer = jackson.readValue(response.body(), Film.class);
		assertEquals(200, response.statusCode());
		assertEquals(film, filmFromServer);
	}

	@Order(9)
	@Test
	void shouldDeleteUserById() throws IOException, InterruptedException {
		User user = User.builder()
				.email("romashka83@gmail.com")
				.login("ChUkOtSkAyA_DeVoChKa")
				.birthday(LocalDate.of(1983, 12, 12))
				.build();
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
		user = user.toBuilder().id(2).name("ChUkOtSkAyA_DeVoChKa").build();
		assertEquals(user, userFromServer);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/id1"))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		userFromServer = jackson.readValue(response.body(), User.class);
		assertEquals(200, response.statusCode());
		assertEquals(User.builder().id(1).email("sexmaster96@gmail.com").login("narkoman_pavlik").
			name("narkoman_pavlik").birthday(LocalDate.of(1996, 12, 12)).build(), userFromServer);
	}

	@Order(10)
	@Test
	void shouldDeleteFilmById() throws IOException, InterruptedException {
		Film film = Film.builder()
				.name("Cartman's mom")
				.description("Adventures of one woman with american football team")
				.releaseDate(LocalDate.of(2013, 5, 15))
				.duration(95)
				.build();
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
		film = film.toBuilder().id(2).build();
		assertEquals(film, filmFromServer);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/id1"))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		filmFromServer = jackson.readValue(response.body(), Film.class);
		assertEquals(200, response.statusCode());
		assertEquals(Film.builder().id(1).name("Whores & holes").description("Adventures of women in video world")
				.releaseDate(LocalDate.of(1996, 12, 12)).duration(125).build(), filmFromServer);
	}

	@Order(11)
	@Test
	void shouldNotDoSomethongWithUserWhenIncorrectId() throws IOException, InterruptedException {
		User user = User.builder()
				.id(777)
				.email("romashka83@gmail.com")
				.login("ChUkOtSkAyA_DeVoChKa")
				.birthday(LocalDate.of(1983, 12, 12))
				.build();
		String userJson = jackson.writeValueAsString(user);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(userJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(500, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/identifier1"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users/id77.77"))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());
	}

	@Order(12)
	@Test
	void shouldNotDoSomethingWithFilmWhenIncorrectId() throws IOException, InterruptedException {
		Film film = Film.builder()
				.id(777)
				.name("Cartman's mom")
				.description("Adventures of one woman with american football team")
				.releaseDate(LocalDate.of(2013, 5, 15))
				.duration(95)
				.build();
		String filmJson = jackson.writeValueAsString(film);
		bodyPublisher = HttpRequest.BodyPublishers.ofString(filmJson);
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.PUT(bodyPublisher)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(500, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/id777"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films/id_Abcdef"))
				.DELETE()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		assertEquals(404, response.statusCode());
	}

	@Order(13)
	@Test
	void shouldDeleteAllUsers() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		User[] usersFromServer = jackson.readValue(response.body(), User[].class);
		assertEquals(1, usersFromServer.length);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "users"))
				.DELETE()
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
		usersFromServer = jackson.readValue(response.body(), User[].class);
		assertEquals(0, usersFromServer.length);
	}

	@Order(14)
	@Test
	void shouldDeleteAllFilms() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.GET()
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-type", "application/json")
				.build();
		response = client.send(request, BODY_HANDLER);
		Film[] filmsFromServer = jackson.readValue(response.body(), Film[].class);
		assertEquals(1, filmsFromServer.length);

		request = HttpRequest.newBuilder()
				.uri(URI.create(URL_START + "films"))
				.DELETE()
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
		filmsFromServer = jackson.readValue(response.body(), Film[].class);
		assertEquals(0, filmsFromServer.length);
	}

}
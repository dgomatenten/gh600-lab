# Backend (Spring Boot)

**Location:** [backend/](../backend/)

A Spring Boot REST API. It serves JSON under `/api` and will store questions and study progress in a file-based H2 database.

## Stack

| Item | Version / choice |
|------|------------------|
| Java | 25 |
| Spring Boot | 4.1.1 (`spring-boot-starter-parent`) |
| Build tool | Maven, via the wrapper (`mvnw` / `mvnw.cmd`) |
| Web | `spring-boot-starter-webmvc` (embedded Tomcat, Jackson JSON) |
| Persistence | `spring-boot-starter-data-jpa` + H2 (runtime) |
| Validation | `spring-boot-starter-validation` |
| Dev | `spring-boot-devtools` (auto-restart on recompile), `spring-boot-h2console` |

Package root: `com.gh600.study`. Spring only finds components (controllers, services and so on) that live in this package or below it.

## Key files

| File | Purpose |
|------|---------|
| [BackendApplication.java](../backend/src/main/java/com/gh600/study/BackendApplication.java) | Entry point (`@SpringBootApplication`) |
| [HelloController.java](../backend/src/main/java/com/gh600/study/HelloController.java) | First REST endpoint (see [hello-endpoint.md](hello-endpoint.md)) |
| [question/](../backend/src/main/java/com/gh600/study/question/) | JPA entities, `QuestionRepository`, `QuestionService`, `QuestionController`, DTOs, validation & error handling (see [question-model.md](question-model.md) and [questions-api.md](questions-api.md)) |
| [application.properties](../backend/src/main/resources/application.properties) | Configuration |
| [pom.xml](../backend/pom.xml) | Dependencies and build |

## Configuration

Set in `application.properties`:

| Property | Value | Meaning |
|----------|-------|---------|
| `server.port` | `8080` | HTTP port |
| `spring.datasource.url` | `jdbc:h2:file:./data/gh600` | H2 database stored on disk in `backend/data/`, so data survives restarts (the folder is gitignored) |
| `spring.datasource.username` / `password` | `sa` / *(empty)* | H2 credentials |
| `spring.h2.console.enabled` | `true` | DB browser at http://localhost:8080/h2-console |
| `spring.jpa.hibernate.ddl-auto` | `update` | Hibernate creates or alters tables to match `@Entity` classes. This is fine for learning; switch to Flyway migrations later |
| `spring.jpa.show-sql` / `format_sql` | `true` | Logs the generated SQL |

**H2 console login:** use JDBC URL `jdbc:h2:file:./data/gh600`, user `sa`, and an empty password.

## Run

From `backend/`:

```powershell
.\mvnw.cmd spring-boot:run
```

Check it's running: http://localhost:8080/api/hello

## Test

```powershell
.\mvnw.cmd test
```

| Test | What it checks |
|------|----------------|
| [BackendApplicationTests](../backend/src/test/java/com/gh600/study/BackendApplicationTests.java) | The full Spring context starts and seeds sample questions (`@SpringBootTest`) |
| [HelloControllerTest](../backend/src/test/java/com/gh600/study/HelloControllerTest.java) | `/api/hello` responses (web layer only, see [hello-endpoint.md](hello-endpoint.md)) |
| [QuestionRepositoryTest](../backend/src/test/java/com/gh600/study/question/QuestionRepositoryTest.java) | JPA entities, cascade save, `orphanRemoval`, derived queries, and `JOIN FETCH` (`@DataJpaTest`, see [question-model.md](question-model.md)) |
| [QuestionControllerTest](../backend/src/test/java/com/gh600/study/question/QuestionControllerTest.java) | `/api/questions` CRUD routes, validation (`400`), and `404` `ProblemDetail` (`@WebMvcTest` + `@MockitoBean`, see [questions-api.md](questions-api.md)) |

## Build a jar

```powershell
.\mvnw.cmd package
java -jar target\backend-0.0.1-SNAPSHOT.jar
```

# Study Guide — Spring Boot + React by Building the GH-600 App

You learn Spring Boot and React by building a real app: a study tool for the GH-600 exam. It stores practice questions, runs quizzes and tracks your progress.

Each **milestone** adds one feature and teaches a set of concepts. Finished milestones point to the actual code in this repo, so you can read how each concept is used. The planned ones list what you'll learn, what to build and how to check yourself.

> **How to use this guide:** work through the milestones in order. For each one:
> 1. Read the concepts.
> 2. Build the feature, or read the linked code if it's already built.
> 3. Answer the self-check questions without looking.
> 4. Tick the box.
>
> This guide is updated each time a new milestone is built.

## Progress

| # | Milestone | Backend (Spring Boot) | Frontend (React) | Status |
|---|-----------|-----------------------|------------------|--------|
| 0 | [Hello, full stack](#milestone-0--hello-full-stack) | App startup, REST controller, JSON, config, tests | Components, state, effects, fetch, proxy | ✅ Done |
| 1 | [Question model & database](#milestone-1--question-model--database) | JPA entities, repositories, H2 | — | ✅ Done |
| 2 | [Questions REST API](#milestone-2--questions-rest-api) | Service layer, DTOs, validation, error handling | — | ✅ Done |
| 3 | [Question list page](#milestone-3--question-list-page) | Paging and filtering | Props, lists and keys, custom hooks | ✅ Done |
| 4 | [Routing & question detail](#milestone-4--routing--question-detail) | Path variables, 404s | React Router, URL params | ✅ Done |
| 5 | [Import practice tests](#milestone-5--import-practice-tests) | File upload, parsing, transactions | Forms, file input, upload progress | ⬜ Next |
| 6 | [Quiz mode](#milestone-6--quiz-mode) | Business logic, relationships | Complex state, `useReducer`, lifting state | ⬜ |
| 7 | [Progress tracking](#milestone-7--progress-tracking) | Aggregation queries, projections | Charts, derived state | ⬜ |
| 8 | [Testing in depth](#milestone-8--testing-in-depth) | `@DataJpaTest`, service tests, mocking | Vitest + React Testing Library | ⬜ |
| 9 | [Production readiness](#milestone-9--production-readiness) | Flyway, profiles, packaging | Production build served by Spring Boot | ⬜ |

---

## Milestone 0 — Hello, full stack

**Status:** ✅ Built. Docs: [backend.md](backend.md), [hello-endpoint.md](hello-endpoint.md), [frontend.md](frontend.md)

**What it does:** the React page calls `GET /api/hello` and shows the greeting and the server's time.

### Spring Boot concepts

| Concept | Where to see it | Key idea |
|---------|-----------------|----------|
| `@SpringBootApplication` | [BackendApplication.java](../backend/src/main/java/com/gh600/study/BackendApplication.java) | Combines three things: configuration, **auto-configuration** (Boot sets up Tomcat, Jackson and JPA from what's on the classpath) and **component scanning** (it finds your classes in `com.gh600.study` and below). |
| Starters | [pom.xml](../backend/pom.xml) | `spring-boot-starter-webmvc` pulls in everything needed for a web API. The version comes from the parent POM. |
| `@RestController` + `@RequestMapping` | [HelloController.java](../backend/src/main/java/com/gh600/study/HelloController.java) | Spring creates **one instance** (a *bean*) and sends matching HTTP requests to its methods. Return values become JSON. |
| `@GetMapping` + `@RequestParam` | same | Map `GET /api/hello?name=X` to a method argument, with a default value. |
| Java `record` as a DTO | `HelloResponse` in same file | An immutable data carrier. Jackson serialises it to `{"message":…,"serverTime":…}`. |
| `application.properties` | [application.properties](../backend/src/main/resources/application.properties) | Each key overrides an auto-configured default: port, datasource, JPA behaviour. |
| Slice test: `@WebMvcTest` + `MockMvc` | [HelloControllerTest.java](../backend/src/test/java/com/gh600/study/HelloControllerTest.java) | Loads **only the web layer** and sends fake HTTP requests, so it's fast. |
| Full context test: `@SpringBootTest` | [BackendApplicationTests.java](../backend/src/test/java/com/gh600/study/BackendApplicationTests.java) | Starts the whole app, which catches wiring and configuration mistakes. |

### React concepts

| Concept | Where to see it | Key idea |
|---------|-----------------|----------|
| Component + JSX | [App.tsx](../frontend/src/App.tsx) | A component is a function that returns markup. React calls it again (**re-renders**) when its state changes. |
| `useState` | `hello`, `error`, `name` in App.tsx | A value React remembers between renders. Calling the setter updates it **and** triggers a re-render. |
| `useEffect(fn, [])` | App.tsx | Runs side effects such as network calls *after* rendering. `[]` means "only once, when the component first appears". |
| `fetch` + `async/await` | `loadHello` in App.tsx | Calls the backend and handles `!res.ok` and network errors. |
| Conditional rendering | `{error && …}` | Shows loading, error or data depending on state. |
| Controlled input | `<input value={name} onChange=…>` | React state is the single source of truth for the input's value. |
| TypeScript types for API data | `type HelloResponse` | Must match the Java record's shape. |
| `StrictMode` | [main.tsx](../frontend/src/main.tsx) | In dev it runs effects **twice** to expose bugs, which is why you'll see two `/api/hello` calls. |
| Vite dev proxy | [vite.config.ts](../frontend/vite.config.ts) | The browser only talks to `:5173`, and Vite forwards `/api` to `:8080`, so there's no CORS setup. |

### Try it

1. Start both apps (see [README.md](README.md#quick-start-local-dev)). Open http://localhost:5173, then the browser DevTools **Network** tab, and watch the `/api/hello` requests.
2. Add a third field to `HelloResponse`, such as `String javaVersion` set from `System.getProperty("java.version")`. Update the test and the TypeScript type, then show it on the page.
3. Break it on purpose. Stop the backend and see the error message. Rename the `@GetMapping` path and watch `HelloControllerTest` fail.

### Self-check

- [x] Why does Spring find `HelloController` without you registering it anywhere?
- [x] What's the difference between `@WebMvcTest` and `@SpringBootTest`, and when would you use each?
- [x] What happens if you remove `[]` from `useEffect`? Why?
- [x] Why doesn't the frontend need CORS configuration in development?
- [x] Why does `/api/hello` get called twice in dev mode?

---

## Milestone 1 — Question model & database

**Status:** ✅ Built. Docs: [question-model.md](question-model.md)

**Goal:** store practice questions in H2 with JPA.

### Spring Data JPA concepts

| Concept | Where to see it | Key idea |
|---------|-----------------|----------|
| `@Entity`, `@Id`, `@GeneratedValue`, `@Column` | [Question.java](../backend/src/main/java/com/gh600/study/question/Question.java), [AnswerOption.java](../backend/src/main/java/com/gh600/study/question/AnswerOption.java) | Maps Java classes to `question` and `answer_option` tables (`ddl-auto=update` creates/updates the schema automatically). |
| `@Enumerated(EnumType.STRING)` | [Domain.java](../backend/src/main/java/com/gh600/study/question/Domain.java) & `Question.domain` | Stores enum names (`"AGENT_ARCHITECTURE_AND_SDLC"`) instead of fragile numeric ordinals (`0, 1, 2...`). |
| Bidirectional `@OneToMany` / `@ManyToOne` | `Question.options` ↔ `AnswerOption.question` | `AnswerOption` owns the foreign key (`question_id`). `Question.addOption(...)` sets both sides so the foreign key is always populated. |
| `cascade = CascadeType.ALL` & `orphanRemoval = true` | `Question.options` | Saving/deleting a `Question` cascades to its options; removing an option from the list deletes its DB row. |
| `JpaRepository<Question, Long>` & derived queries | [QuestionRepository.java](../backend/src/main/java/com/gh600/study/question/QuestionRepository.java) | Free CRUD (`save`, `findById`, `count`) plus derived queries (`findByDomain`, `findByMultiSelectTrue`) generated from method names. |
| `@Query` + `LEFT JOIN FETCH` (N+1 fix) | `findAllWithOptions` in [QuestionRepository.java](../backend/src/main/java/com/gh600/study/question/QuestionRepository.java) | Loads questions and their lazy `options` collection in a single SQL query instead of `1 + N` queries. |
| `CommandLineRunner` seeding | [QuestionDataSeeder.java](../backend/src/main/java/com/gh600/study/question/QuestionDataSeeder.java) | Seeds 3 sample GH-600 questions at startup when `questionRepository.count() == 0`. |
| JPA slice test: `@DataJpaTest` | [QuestionRepositoryTest.java](../backend/src/test/java/com/gh600/study/question/QuestionRepositoryTest.java) | Tests entities, cascades, `orphanRemoval`, and queries against an isolated in-memory H2 instance. |

**Check:** open http://localhost:8080/h2-console and query `SELECT * FROM QUESTION` and `SELECT * FROM ANSWER_OPTION`.

**Self-check:**
- [x] Why use `EnumType.STRING` instead of `ORDINAL`?
- [x] Who owns a bidirectional relationship, and why does `Question.addOption` call `option.setQuestion(this)`?
- [x] What is the N+1 query problem, and how does `LEFT JOIN FETCH` solve it?

---

## Milestone 2 — Questions REST API

**Status:** ✅ Built. Docs: [questions-api.md](questions-api.md)

**Goal:** a CRUD API at `/api/questions`.

### Spring Boot & REST API concepts

| Concept | Where to see it | Key idea |
|---------|-----------------|----------|
| Layered architecture | [QuestionController.java](../backend/src/main/java/com/gh600/study/question/QuestionController.java) → [QuestionService.java](../backend/src/main/java/com/gh600/study/question/QuestionService.java) → [QuestionRepository.java](../backend/src/main/java/com/gh600/study/question/QuestionRepository.java) | Controller handles HTTP, Service handles transactions & DTO conversion, Repository handles SQL/JPA. |
| Constructor injection | `QuestionController`, `QuestionService` | Dependencies are `final` fields passed into a single constructor (no field `@Autowired` needed). |
| DTO records vs. JPA entities | [QuestionDto.java](../backend/src/main/java/com/gh600/study/question/QuestionDto.java), [AnswerOptionDto.java](../backend/src/main/java/com/gh600/study/question/AnswerOptionDto.java) | Prevents infinite JSON recursion, `LazyInitializationException`, and leaking DB internals. |
| `@Transactional` & `readOnly = true` | [QuestionService.java](../backend/src/main/java/com/gh600/study/question/QuestionService.java) | Wraps multi-row writes in an atomic DB transaction; `readOnly = true` optimizes read queries. |
| `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PathVariable`, `ResponseEntity` | [QuestionController.java](../backend/src/main/java/com/gh600/study/question/QuestionController.java) | Maps CRUD verbs and returns `200 OK`, `201 Created` (with `Location` header), and `204 No Content`. |
| Bean Validation (`@Valid`, `@NotBlank`, `@NotNull`, `@Size`) | [CreateQuestionRequest.java](../backend/src/main/java/com/gh600/study/question/CreateQuestionRequest.java), [CreateAnswerOptionRequest.java](../backend/src/main/java/com/gh600/study/question/CreateAnswerOptionRequest.java) | Validates incoming JSON before controller logic runs, including nested `List<@Valid ...>`. |
| Global error handling (`@RestControllerAdvice` + `ProblemDetail`) | [ApiExceptionHandler.java](../backend/src/main/java/com/gh600/study/question/ApiExceptionHandler.java), [NotFoundException.java](../backend/src/main/java/com/gh600/study/question/NotFoundException.java) | Converts `NotFoundException` (`404`) and validation errors (`400`) into RFC 9457 `ProblemDetail` JSON. |
| Web-slice test with `@MockitoBean` | [QuestionControllerTest.java](../backend/src/test/java/com/gh600/study/question/QuestionControllerTest.java) | Tests routing, status codes, validation, and 404 handling in isolation by mocking `QuestionService`. |

**Check:** open http://localhost:8080/api/questions in your browser or run `curl.exe http://localhost:8080/api/questions`.

**Self-check:**
- [x] What does `@Transactional` actually do?
- [x] Why should controllers return DTO records instead of JPA `@Entity` classes?
- [x] What status code should a validation error return? (`400 Bad Request`)

---

## Milestone 3 — Question list page

**Status:** ✅ Built. Docs: [question-list-page.md](question-list-page.md)

**Goal:** a React page listing questions, with a domain filter.

### React concepts

| Concept | Where to see it | Key idea |
|---------|-----------------|----------|
| Splitting into components & props | [DomainFilter.tsx](../frontend/src/components/DomainFilter.tsx), [QuestionList.tsx](../frontend/src/components/QuestionList.tsx), [QuestionCard.tsx](../frontend/src/components/QuestionCard.tsx) | Break a complex page into focused components and pass data/callbacks via typed props. |
| Lifting state up | `selectedDomain` in [App.tsx](../frontend/src/App.tsx) | Parent owns shared state (`selectedDomain`) and passes `selected` + `onChange` to `DomainFilter`. |
| Custom hook (`useQuestions`) | [useQuestions.ts](../frontend/src/hooks/useQuestions.ts) | Encapsulates `questions`, `loading`, and `error` state so components stay clean. |
| Effect dependencies & `AbortController` | [useQuestions.ts](../frontend/src/hooks/useQuestions.ts) | `useEffect(..., [domain])` re-fetches when `domain` changes and aborts superseded requests in its cleanup function. |
| Rendering lists with `.map()` & `key` | [QuestionList.tsx](../frontend/src/components/QuestionList.tsx), [QuestionCard.tsx](../frontend/src/components/QuestionCard.tsx) | Always use a stable database ID (`key={q.id}`) rather than the array index so React associates state with the right item when filtering. |

**Check:** open http://localhost:5173, change the domain filter dropdown, click options on a question card, and click **Show Answer & Explanation**.

**Self-check:**
- [x] Why is using the array index as `key` risky when filtering a list of stateful components?
- [x] What goes wrong if you leave `domain` out of `useEffect`'s dependency array `[domain]`?
- [x] Why does `useQuestions` return `() => controller.abort()` from `useEffect`?

---

## Milestone 4 — Routing & question detail

**Status:** ✅ Built. Docs: [routing-and-detail.md](routing-and-detail.md)

**Goal:** `/questions` shows the list and `/questions/:id` shows one question.

### React Router & full-stack routing concepts

| Concept | Where to see it | Key idea |
|---------|-----------------|----------|
| `BrowserRouter`, `Routes`, `Route`, `Navigate` | [App.tsx](../frontend/src/App.tsx) | Maps URL paths (`/questions`, `/questions/:id`) to page components using the browser History API. |
| Shared layout & `<Outlet />` | [Layout.tsx](../frontend/src/components/Layout.tsx) | Keeps the header, navigation, and `/api/hello` bar mounted while swapping child route pages inside `<Outlet />`. |
| Pages vs. components | [QuestionsPage.tsx](../frontend/src/pages/QuestionsPage.tsx), [QuestionDetailPage.tsx](../frontend/src/pages/QuestionDetailPage.tsx) | Pages represent full routes and read URL state; components (`QuestionCard`, `DomainFilter`) are reusable UI pieces passed props. |
| `useParams` & `useNavigate` | [QuestionDetailPage.tsx](../frontend/src/pages/QuestionDetailPage.tsx) | `useParams` extracts `:id` from `/questions/:id`; `useNavigate` drives the Prev/Next buttons. |
| `useSearchParams` | [QuestionsPage.tsx](../frontend/src/pages/QuestionsPage.tsx) | Syncs the domain filter with `?domain=...` in the URL so filters survive Back/Forward navigation. |
| 404 handling end to end | [QuestionController.java](../backend/src/main/java/com/gh600/study/question/QuestionController.java) → [useQuestion.ts](../frontend/src/hooks/useQuestion.ts) → [QuestionDetailPage.tsx](../frontend/src/pages/QuestionDetailPage.tsx) | Backend returns `404 ProblemDetail`; `useQuestion` sets `notFound = true` and the page renders a friendly 404 card. |

**Check:** open http://localhost:5173/questions, click **Question #1 →** on a card to visit `/questions/1`, click **Next →** until you reach `/questions/4` to see the friendly 404 state, then click **← Back to all questions**.

**Self-check:**
- [x] Why does refreshing `/questions/5` work with the Vite dev server but might break in production? (Vite falls back to `index.html` for SPA routes; Spring Boot needs a fallback controller in Milestone 9.)
- [x] Why use `<Link to="...">` instead of `<a href="...">` for internal navigation?

---

## Milestone 5 — Import practice tests

**Goal:** upload a practice-test file and have its questions saved.

**Learn:**
- `MultipartFile` and `@PostMapping(consumes = MULTIPART_FORM_DATA)`
- Parsing: an `.odt` file is a ZIP containing `content.xml`. Use `java.util.zip` and an XML parser. A simpler start is to accept JSON.
- `@Transactional` rollback: either every question imports or none do
- Idempotency: don't import duplicates twice
- React: `<input type="file">`, `FormData`, disabling the button while uploading, and showing the result summary

**Self-check:** What happens to already-saved rows if question 37 fails to parse?

---

## Milestone 6 — Quiz mode

**Goal:** take a timed quiz, answer questions and see a score.

**Learn:**
- Backend: `QuizSession` and `QuizAnswer` entities, scoring logic in a service, and `@ManyToOne` to `Question`
- React: `useReducer` for multi-step state (`START → ANSWERING → REVIEW → DONE`), **lifting state up**, and handling single- vs. multi-select answers
- Timers with `useEffect` cleanup (`clearInterval`)

**Self-check:** When is `useReducer` better than several `useState` calls? Why must an effect return a cleanup function for timers?

---

## Milestone 7 — Progress tracking

**Goal:** a dashboard showing accuracy by domain and your weakest topics.

**Learn:**
- JPQL aggregation (`GROUP BY`, `COUNT`, `AVG`) and **interface or record projections**
- React: derived state (compute it from what you have, don't store it), `useMemo`, and a chart library such as Recharts

---

## Milestone 8 — Testing in depth

**Learn:**
- `@DataJpaTest` for repository tests, unit tests for services with Mockito, and full integration tests with `@SpringBootTest` + `MockMvc`
- Frontend: Vitest + React Testing Library. Render a component, simulate a click and assert on the text. Mock `fetch`.

---

## Milestone 9 — Production readiness

**Learn:**
- **Flyway** migrations to replace `ddl-auto=update`
- **Profiles**: `application-dev.properties` vs. `application-prod.properties`
- Serve the React build from Spring Boot: copy `frontend/dist` to `src/main/resources/static`, and add a fallback so client-side routes reach `index.html`
- `mvnw package` produces one runnable jar

---

## Reference: key commands

| Task | Command |
|------|---------|
| Run backend | `cd backend; .\mvnw.cmd spring-boot:run` |
| Backend tests | `cd backend; .\mvnw.cmd test` |
| Run frontend | `cd frontend; npm run dev` |
| Type-check + build frontend | `cd frontend; npm run build` |
| Lint frontend | `cd frontend; npm run lint` |
| H2 console | http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/gh600`, user `sa`) |

## Reference: learning resources

- Spring Boot reference docs — https://docs.spring.io/spring-boot/
- Spring guides (short, hands-on) — https://spring.io/guides
- Spring Data JPA reference — https://docs.spring.io/spring-data/jpa/reference/
- React docs (learn section) — https://react.dev/learn
- Vite guide — https://vite.dev/guide/
- TypeScript handbook — https://www.typescriptlang.org/docs/handbook/

> The content for the GH-600 exam itself (the questions your app will hold) is kept in a separate private repository.

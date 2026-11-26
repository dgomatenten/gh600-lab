# GH-600 Study App — Docs (public lab copy)

> Public lab copy of a private study repo, used for hands-on GitHub governance labs. Practice-test material is intentionally not included.

A study app for the GH-600 exam, built with a Spring Boot backend and a React frontend.

## Artifacts

| Doc | What it covers |
|-----|----------------|
| [codebase-structure.md](codebase-structure.md) | **Codebase architecture & file guide**: end-to-end request flow, directory tree, and every backend/frontend file explained |
| [backend.md](backend.md) | Spring Boot app: stack, configuration, H2 database, how to run and test |
| [hello-endpoint.md](hello-endpoint.md) | `GET /api/hello`, the first REST endpoint, and its tests |
| [question-model.md](question-model.md) | Milestone 1: JPA entities (`Question`, `AnswerOption`, `Domain`), `QuestionRepository`, and H2 seeding |
| [questions-api.md](questions-api.md) | Milestone 2: `/api/questions` CRUD REST API, DTOs, Bean Validation, and `ProblemDetail` error handling |
| [question-list-page.md](question-list-page.md) | Milestone 3: React `QuestionList`, `QuestionCard`, `DomainFilter`, and `useQuestions` custom hook |
| [routing-and-detail.md](routing-and-detail.md) | Milestone 4: React Router (`/questions`, `/questions/:id`), `<Layout>` with `<Outlet>`, and 404 handling |
| [frontend.md](frontend.md) | React + Vite app: dev proxy, `App` component, how to run, build and lint |
| [start-script.md](start-script.md) | Root startup script (`start.ps1` / `start.cmd`) to launch or stop both servers |
| [study-guide.md](study-guide.md) | **Learning guide**: Spring Boot and React concepts, taught milestone by milestone while building this app |

## Quick start (local dev)

Run both apps with the startup script from the repository root:

```powershell
.\start.ps1
```

Or run the two apps in separate terminals:

```powershell
# Terminal 1 — backend on http://localhost:8080
cd backend
.\mvnw.cmd spring-boot:run

# Terminal 2 — frontend on http://localhost:5173
cd frontend
npm install
npm run dev
```

Then open http://localhost:5173. The page should greet you with a message from the backend.

## Repository layout

```
backend/   Spring Boot (Java 25, Maven)
frontend/  React 19 + TypeScript + Vite
docs/      This documentation
```

## Convention

Every new artifact (feature, endpoint, component, script, config) gets its own `docs/<name>.md` and a row in the table above.

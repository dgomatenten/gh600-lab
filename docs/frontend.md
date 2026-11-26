# Frontend (React + Vite)

**Location:** [frontend/](../frontend/)

A single-page React app that talks to the Spring Boot backend through Vite's dev proxy.

## Stack

| Item | Version |
|------|---------|
| React / React DOM | 19.2 |
| TypeScript | ~6.0 |
| Vite | 8.3 (with `@vitejs/plugin-react`) |
| Linter | oxlint (config in [.oxlintrc.json](../frontend/.oxlintrc.json)) |

## Key files

| File | Purpose |
|------|---------|
| [index.html](../frontend/index.html) | HTML shell with `<div id="root">` |
| [src/main.tsx](../frontend/src/main.tsx) | Mounts `<App />` inside `StrictMode` |
| [src/App.tsx](../frontend/src/App.tsx) | Root `<BrowserRouter>` and `<Routes>` (`/questions`, `/questions/:id`, 404 fallback; see [routing-and-detail.md](routing-and-detail.md)) |
| [src/components/Layout.tsx](../frontend/src/components/Layout.tsx) | Shared header, `<NavLink>` bar, `/api/hello` greeting, and `<Outlet />` |
| [src/pages/QuestionsPage.tsx](../frontend/src/pages/QuestionsPage.tsx) | Route page for `/questions` (`useSearchParams` domain filter + `QuestionList`) |
| [src/pages/QuestionDetailPage.tsx](../frontend/src/pages/QuestionDetailPage.tsx) | Route page for `/questions/:id` (`useParams`, `useNavigate`, and 404 handling) |
| [src/types/question.ts](../frontend/src/types/question.ts) | TypeScript types (`Domain`, `Question`, `AnswerOption`, `DOMAINS`) matching backend DTOs |
| [src/hooks/useQuestions.ts](../frontend/src/hooks/useQuestions.ts) | Custom hook fetching `GET /api/questions` with `AbortController` cleanup (see [question-list-page.md](question-list-page.md)) |
| [src/hooks/useQuestion.ts](../frontend/src/hooks/useQuestion.ts) | Custom hook fetching `GET /api/questions/:id` with 404 detection |
| [src/components/DomainFilter.tsx](../frontend/src/components/DomainFilter.tsx) | Controlled domain `<select>` filter and question count badge |
| [src/components/QuestionList.tsx](../frontend/src/components/QuestionList.tsx) | Renders loading/error/empty states or the list of `<QuestionCard>` items |
| [src/components/QuestionCard.tsx](../frontend/src/components/QuestionCard.tsx) | Interactive question card with option selection and answer/explanation reveal |
| [src/App.css](../frontend/src/App.css), [src/index.css](../frontend/src/index.css) | Styles |
| [vite.config.ts](../frontend/vite.config.ts) | Dev server port and API proxy |

## Dev proxy

In [vite.config.ts](../frontend/vite.config.ts), the dev server runs on port **5173** and forwards every `/api/*` request to `http://localhost:8080`.

The browser only talks to `:5173`, so development needs no CORS setup. Frontend code should call relative URLs such as `fetch('/api/hello')` and never hard-code `localhost:8080`.

> The proxy exists only in `npm run dev`. A production build will need the backend to serve the built files or a reverse proxy. This isn't set up yet.

## `App` component

[src/App.tsx](../frontend/src/App.tsx):

- On mount (`useEffect` with `[]`), it calls `GET /api/hello` and shows the message and the server time in the local timezone.
- A name input (a controlled `<input>`) and a **Say hello** button call `GET /api/hello?name=<name>`.
- It shows **Loading…** while the request is pending, and an error message if the backend can't be reached (for example, when Spring Boot isn't running).
- The `HelloResponse` TypeScript type mirrors the backend's Java record. See [hello-endpoint.md](hello-endpoint.md).

## Commands

From `frontend/`:

| Command | What it does |
|---------|--------------|
| `npm install` | Install dependencies (first time only) |
| `npm run dev` | Start the dev server at http://localhost:5173 with hot reload |
| `npm run build` | Type-check (`tsc -b`) and build to `dist/` |
| `npm run preview` | Serve the built `dist/` locally |
| `npm run lint` | Run oxlint |

Start the backend first (see [backend.md](backend.md)). Otherwise the page shows the "Could not reach the backend" error.

## Test

No automated frontend tests yet. To check by hand: run both apps, open http://localhost:5173, confirm the default greeting appears, then enter a name and click **Say hello**.

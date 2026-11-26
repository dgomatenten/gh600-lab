# Hello endpoint — `GET /api/hello`

**Code:** [HelloController.java](../backend/src/main/java/com/gh600/study/HelloController.java)
**Tests:** [HelloControllerTest.java](../backend/src/test/java/com/gh600/study/HelloControllerTest.java)
**Used by:** [App.tsx](../frontend/src/App.tsx) (see [frontend.md](frontend.md))

The first REST endpoint. It returns a greeting and the server's current time, and proves the backend and frontend are wired together end to end.

## API

```
GET /api/hello[?name=<string>]
```

| Query param | Required | Default | Description |
|-------------|----------|---------|-------------|
| `name` | no | `GH-600 learner` | Who to greet |

**Response** `200 OK`, `application/json`:

```json
{
  "message": "Hello, Ruru!",
  "serverTime": "2026-09-28T10:15:30.123Z"
}
```

| Field | Type | Notes |
|-------|------|-------|
| `message` | string | `"Hello, " + name + "!"` |
| `serverTime` | string | A Java `Instant`, serialized as ISO-8601 UTC |

## Examples

```powershell
curl http://localhost:8080/api/hello
curl "http://localhost:8080/api/hello?name=Ruru"
```

## Implementation notes

- `@RestController` + `@RequestMapping("/api")`: return values are written as JSON, and every route in the class is prefixed with `/api`.
- The response shape is the Java record `HelloResponse(String message, Instant serverTime)`. The frontend's `HelloResponse` TypeScript type must stay in sync with it.
- `@RequestParam(defaultValue = "GH-600 learner")` binds `?name=`.

## Tests

`HelloControllerTest` uses `@WebMvcTest`, which loads only the web layer (no database), and `MockMvc`:

| Test | Request | Expects |
|------|---------|---------|
| `greetsByName` | `GET /api/hello?name=Ruru` | 200, `message = "Hello, Ruru!"` |
| `usesDefaultName` | `GET /api/hello` | 200, `message = "Hello, GH-600 learner!"` |

Run only these tests:

```powershell
cd backend
.\mvnw.cmd test -Dtest=HelloControllerTest
```

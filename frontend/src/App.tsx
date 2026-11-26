import { BrowserRouter, Link, Navigate, Route, Routes } from 'react-router-dom'
import './App.css'
import { Layout } from './components/Layout.tsx'
import { QuestionDetailPage } from './pages/QuestionDetailPage.tsx'
import { QuestionsPage } from './pages/QuestionsPage.tsx'

/**
 * Root router configuration (Milestone 4).
 *
 * Routes:
 *   - `/`              -> Redirects to `/questions`
 *   - `/questions`     -> QuestionsPage (list + domain filter)
 *   - `/questions/:id` -> QuestionDetailPage (single question + 404 handling)
 *   - `*`              -> Catch-all 404 route for unknown client-side URLs
 */
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<Navigate to="/questions" replace />} />
          <Route path="/questions" element={<QuestionsPage />} />
          <Route path="/questions/:id" element={<QuestionDetailPage />} />
          <Route
            path="*"
            element={
              <div className="status-box not-found" role="alert">
                <h2>Page Not Found (404)</h2>
                <p>No route matches this URL.</p>
                <p style={{ marginTop: '1rem' }}>
                  <Link to="/questions" className="btn-reveal link-btn">
                    Go to Questions
                  </Link>
                </p>
              </div>
            }
          />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App

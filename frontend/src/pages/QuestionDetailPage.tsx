import { Link, useNavigate, useParams } from 'react-router-dom'
import { QuestionCard } from '../components/QuestionCard.tsx'
import { useQuestion } from '../hooks/useQuestion.ts'

/**
 * Page component for `/questions/:id` (Milestone 4).
 *
 * Key React Router hooks used:
 *   - useParams(): reads `:id` from the URL path (e.g. `/questions/2` -> `id = "2"`).
 *   - useNavigate(): navigates programmatically (e.g. Previous / Next buttons).
 *   - <Link to="/questions">: client-side link back to the list without a full page reload.
 */
export function QuestionDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { question, loading, notFound, error } = useQuestion(id)

  const numericId = Number(id)

  return (
    <section className="detail-page">
      <div className="detail-toolbar">
        <Link to="/questions" className="btn-secondary link-btn">
          ← Back to all questions
        </Link>

        {! Number.isNaN(numericId) && (
          <div className="detail-pager">
            <button
              type="button"
              className="btn-secondary"
              disabled={numericId <= 1}
              onClick={() => navigate(`/questions/${numericId - 1}`)}
            >
              ← Prev (#{Math.max(1, numericId - 1)})
            </button>
            <button
              type="button"
              className="btn-secondary"
              onClick={() => navigate(`/questions/${numericId + 1}`)}
            >
              Next (#{numericId + 1}) →
            </button>
          </div>
        )}
      </div>

      {loading && <p className="status-box">Loading question #{id}…</p>}

      {error && (
        <div className="status-box error" role="alert">
          {error}
        </div>
      )}

      {notFound && !loading && (
        <div className="status-box not-found" role="alert">
          <h2>Question #{id} not found (404)</h2>
          <p>
            The backend returned <code>404 Not Found</code> because no question exists with ID{' '}
            <strong>{id}</strong>.
          </p>
          <p style={{ marginTop: '1rem' }}>
            <Link to="/questions" className="btn-reveal link-btn">
              Browse available questions
            </Link>
          </p>
        </div>
      )}

      {question && !loading && (
        <QuestionCard
          key={question.id}
          question={question}
          index={question.id - 1}
          showDetailLink={false}
        />
      )}
    </section>
  )
}


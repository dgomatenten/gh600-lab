import type { Question } from '../types/question.ts'
import { QuestionCard } from './QuestionCard.tsx'

type QuestionListProps = {
  questions: Question[]
  loading: boolean
  error: string | null
}

/**
 * Renders the list of QuestionCards, or a loading / error / empty state.
 *
 * Why `key={q.id}` on <QuestionCard>?
 *   React uses `key` to match component instances across renders when a list is
 *   filtered or reordered. If you used the array index (`key={idx}`), filtering
 *   from 3 questions down to 1 would make React reuse the first QuestionCard's
 *   internal state (`showAnswer`, `selectedOptionIds`) for a completely different question!
 */
export function QuestionList({ questions, loading, error }: QuestionListProps) {
  if (error) {
    return (
      <div className="status-box error" role="alert">
        {error}
      </div>
    )
  }

  if (loading && questions.length === 0) {
    return <p className="status-box">Loading questions…</p>
  }

  if (questions.length === 0) {
    return (
      <div className="status-box empty">
        No questions found for the selected domain.
      </div>
    )
  }

  return (
    <section className="question-list" aria-label="Practice questions">
      {questions.map((q, idx) => (
        <QuestionCard key={q.id} question={q} index={idx} />
      ))}
    </section>
  )
}


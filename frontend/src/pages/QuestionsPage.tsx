import { useSearchParams } from 'react-router-dom'
import { DomainFilter } from '../components/DomainFilter.tsx'
import { QuestionList } from '../components/QuestionList.tsx'
import { useQuestions } from '../hooks/useQuestions.ts'
import type { Domain } from '../types/question.ts'

/**
 * Page component for `/questions` (Milestone 4).
 *
 * Stores the selected domain filter in the URL query string (`?domain=...`) via
 * React Router's `useSearchParams` hook. That way:
 *   - Bookmarking or sharing `/questions?domain=GUARDRAILS_AND_ACCOUNTABILITY` works
 *   - Clicking into `/questions/1` and pressing the browser Back button restores your filter!
 */
export function QuestionsPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const selectedDomain = (searchParams.get('domain') as Domain | null) ?? ''

  const { questions, loading, error } = useQuestions(selectedDomain)

  function handleDomainChange(nextDomain: Domain | '') {
    if (nextDomain) {
      setSearchParams({ domain: nextDomain })
    } else {
      setSearchParams({})
    }
  }

  return (
    <>
      <DomainFilter
        selected={selectedDomain}
        onChange={handleDomainChange}
        count={questions.length}
        loading={loading}
      />
      <QuestionList questions={questions} loading={loading} error={error} />
    </>
  )
}


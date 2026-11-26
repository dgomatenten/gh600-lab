import { useEffect, useState } from 'react'
import type { Question } from '../types/question.ts'

/**
 * Custom React Hook: useQuestion(id)
 *
 * Fetches a single Question from GET /api/questions/:id.
 * Distinguishes between:
 *   - 404 Not Found (`notFound = true`) so the UI can show a friendly "Question not found" state
 *   - Other HTTP or network errors (`error = "..."`)
 */
export function useQuestion(id: string | undefined) {
  const [question, setQuestion] = useState<Question | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [notFound, setNotFound] = useState<boolean>(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const controller = new AbortController()

    async function fetchOne() {
      if (!id) {
        setLoading(false)
        setNotFound(true)
        return
      }

      try {
        setLoading(true)
        setNotFound(false)
        setError(null)

        const res = await fetch(`/api/questions/${encodeURIComponent(id)}`, {
          signal: controller.signal,
        })

        if (res.status === 404) {
          setQuestion(null)
          setNotFound(true)
          return
        }

        if (!res.ok) {
          throw new Error(`HTTP ${res.status}`)
        }

        const data: Question = await res.json()
        setQuestion(data)
      } catch (err) {
        if ((err as Error).name === 'AbortError') {
          return
        }
        setError(
          `Could not load question #${id} (${(err as Error).message}). Is the backend running?`,
        )
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false)
        }
      }
    }

    fetchOne()

    return () => {
      controller.abort()
    }
  }, [id])

  return { question, loading, notFound, error }
}

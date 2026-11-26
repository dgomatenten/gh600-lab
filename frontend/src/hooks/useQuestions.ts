import { useEffect, useState } from 'react'
import type { Domain, Question } from '../types/question.ts'

/**
 * Custom React Hook: useQuestions(domain)
 *
 * A custom hook is a function starting with "use" that calls built-in hooks
 * (useState, useEffect) so components stay focused on rendering UI rather than
 * managing fetch state.
 *
 * Key concepts demonstrated here:
 *   1. Dependency array [domain]: React re-runs the effect whenever `domain` changes.
 *   2. AbortController cleanup: if the user switches domains quickly (or React StrictMode
 *      mounts/unmounts in dev), the cleanup function `() => controller.abort()` cancels
 *      the previous in-flight request so an older, slower response never overwrites
 *      the newer one!
 */
export function useQuestions(domain: Domain | '') {
  const [questions, setQuestions] = useState<Question[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const controller = new AbortController()

    async function fetchQuestions() {
      try {
        setLoading(true)
        setError(null)

        const query = domain ? `?domain=${encodeURIComponent(domain)}` : ''
        const res = await fetch(`/api/questions${query}`, {
          signal: controller.signal,
        })

        if (!res.ok) {
          throw new Error(`HTTP ${res.status}`)
        }

        const data: Question[] = await res.json()
        setQuestions(data)
      } catch (err) {
        // Ignore AbortError — it's expected when cancelling a superseded request
        if ((err as Error).name === 'AbortError') {
          return
        }
        setError(
          `Could not load questions (${(err as Error).message}). Is the Spring Boot backend running on :8080?`,
        )
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false)
        }
      }
    }

    fetchQuestions()

    // Effect cleanup: runs before the next effect execution and on component unmount
    return () => {
      controller.abort()
    }
  }, [domain])

  return { questions, loading, error }
}


import { useEffect, useState } from 'react'
import { Link, NavLink, Outlet } from 'react-router-dom'

type HelloResponse = {
  message: string
  serverTime: string
}

/**
 * Shared application layout (Milestone 4).
 *
 * React Router renders <Layout /> for all child routes and plugs the matching
 * child page (<QuestionsPage /> or <QuestionDetailPage />) into <Outlet />.
 * Because <Layout /> stays mounted when you navigate between /questions and
 * /questions/1, the header and /api/hello greeting state are preserved without
 * re-fetching!
 */
export function Layout() {
  const [hello, setHello] = useState<HelloResponse | null>(null)
  const [name, setName] = useState('')

  async function loadHello(who?: string) {
    try {
      const query = who ? `?name=${encodeURIComponent(who)}` : ''
      const res = await fetch(`/api/hello${query}`)
      if (res.ok) {
        setHello(await res.json())
      }
    } catch {
      // Page-level hooks display a detailed error if the backend is unreachable
    }
  }

  useEffect(() => {
    let cancelled = false
    async function initHello() {
      try {
        const res = await fetch('/api/hello')
        if (res.ok && !cancelled) {
          setHello(await res.json())
        }
      } catch {
        // Ignored here
      }
    }
    initHello()
    return () => {
      cancelled = true
    }
  }, [])

  return (
    <div className="app">
      <header className="app-header">
        <div>
          <Link to="/questions" className="brand-link">
            <h1>GH-600 Study App</h1>
          </Link>
          <p className="subtitle">
            GitHub Copilot &amp; Agentic AI Certification Practice (Spring Boot + React)
          </p>
          <nav className="main-nav" aria-label="Main navigation">
            <NavLink
              to="/questions"
              end
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            >
              All Questions
            </NavLink>
            <NavLink
              to="/questions/1"
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            >
              Question #1
            </NavLink>
          </nav>
        </div>

        <div className="hello-bar">
          {hello && (
            <span className="hello-status">
              <strong>{hello.message}</strong> ·{' '}
              <span className="time">
                {new Date(hello.serverTime).toLocaleTimeString()}
              </span>
            </span>
          )}
          <form
            className="hello-form"
            onSubmit={(e) => {
              e.preventDefault()
              loadHello(name)
            }}
          >
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Your name"
              aria-label="Your name"
            />
            <button type="submit" className="btn-secondary">
              Greet
            </button>
          </form>
        </div>
      </header>

      <main>
        {/* Child route pages render here */}
        <Outlet />
      </main>
    </div>
  )
}


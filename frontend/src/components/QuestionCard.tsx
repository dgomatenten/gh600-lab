import { useState } from 'react'
import { Link } from 'react-router-dom'
import type { Question } from '../types/question.ts'

type QuestionCardProps = {
  question: Question
  index: number
  showDetailLink?: boolean
}

/**
 * Renders a single GH-600 question card with interactive option selection
 * and an expandable answer/explanation reveal.
 *
 * Each QuestionCard manages its own local UI state (`selectedOptionIds` and `showAnswer`)
 * because revealing the answer on Question 1 shouldn't affect Question 2.
 */
export function QuestionCard({
  question,
  index,
  showDetailLink = true,
}: QuestionCardProps) {
  const [selectedOptionIds, setSelectedOptionIds] = useState<number[]>([])
  const [showAnswer, setShowAnswer] = useState(false)

  function toggleOption(optionId: number) {
    if (question.multiSelect) {
      setSelectedOptionIds((prev) =>
        prev.includes(optionId)
          ? prev.filter((id) => id !== optionId)
          : [...prev, optionId],
      )
    } else {
      setSelectedOptionIds([optionId])
    }
  }

  return (
    <article className="question-card">
      <header className="question-header">
        <span className="domain-badge">{question.domainTitle}</span>
        <div className="question-header-right">
          <span className={`type-badge ${question.multiSelect ? 'multi' : 'single'}`}>
            {question.multiSelect ? 'Multi-select' : 'Single choice'}
          </span>
          {showDetailLink && (
            <Link to={`/questions/${question.id}`} className="detail-link">
              Question #{question.id} →
            </Link>
          )}
        </div>
      </header>

      <h2 className="question-title">
        <span className="question-num">Q{index + 1}.</span> {question.text}
      </h2>

      <ul className="options-list">
        {question.options.map((opt, optIdx) => {
          const letter = String.fromCharCode(65 + optIdx) // A, B, C, D...
          const isSelected = selectedOptionIds.includes(opt.id)

          let statusClass = ''
          if (showAnswer) {
            statusClass = opt.correct ? 'correct' : isSelected ? 'incorrect' : ''
          } else if (isSelected) {
            statusClass = 'selected'
          }

          return (
            <li key={opt.id} className={`option-item ${statusClass}`}>
              <button
                type="button"
                className="option-button"
                onClick={() => toggleOption(opt.id)}
              >
                <span className="option-letter">{letter}</span>
                <span className="option-text">{opt.text}</span>
                {showAnswer && (
                  <span className="option-verdict" aria-hidden="true">
                    {opt.correct ? '✅' : '❌'}
                  </span>
                )}
              </button>
              {showAnswer && (
                <p className="option-explanation">{opt.explanation}</p>
              )}
            </li>
          )
        })}
      </ul>

      <footer className="question-footer">
        <button
          type="button"
          className="btn-reveal"
          onClick={() => setShowAnswer((prev) => !prev)}
        >
          {showAnswer ? 'Hide Answer & Explanation' : 'Show Answer & Explanation'}
        </button>

        {showAnswer && (
          <div className="overall-explanation">
            <strong>Why:</strong> {question.explanation}
          </div>
        )}
      </footer>
    </article>
  )
}

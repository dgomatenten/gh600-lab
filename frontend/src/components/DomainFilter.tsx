import { DOMAINS, type Domain } from '../types/question.ts'

/**
 * Props for DomainFilter.
 *
 * Notice that DomainFilter does not own `selected` state itself — the parent (`App`)
 * owns the state ("lifting state up") and passes both the current value (`selected`)
 * and a callback (`onChange`) down as props.
 */
type DomainFilterProps = {
  selected: Domain | ''
  onChange: (domain: Domain | '') => void
  count: number
  loading: boolean
}

export function DomainFilter({ selected, onChange, count, loading }: DomainFilterProps) {
  return (
    <div className="filter-bar">
      <div className="filter-control">
        <label htmlFor="domain-select">Filter by domain:</label>
        <select
          id="domain-select"
          value={selected}
          onChange={(e) => onChange(e.target.value as Domain | '')}
        >
          <option value="">All Domains (1–6)</option>
          {DOMAINS.map((d) => (
            <option key={d.value} value={d.value}>
              {d.label}
            </option>
          ))}
        </select>
        {selected && (
          <button
            type="button"
            className="btn-secondary"
            onClick={() => onChange('')}
          >
            Reset
          </button>
        )}
      </div>

      <span className="question-count" aria-live="polite">
        {loading ? 'Loading…' : `${count} ${count === 1 ? 'question' : 'questions'}`}
      </span>
    </div>
  )
}


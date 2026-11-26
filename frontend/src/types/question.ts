/**
 * TypeScript types mirroring the Spring Boot DTO records in com.gh600.study.question:
 *   - Domain.java
 *   - AnswerOptionDto.java
 *   - QuestionDto.java
 */

export type Domain =
  | 'AGENT_ARCHITECTURE_AND_SDLC'
  | 'TOOL_USE_AND_ENVIRONMENT'
  | 'MEMORY_STATE_AND_EXECUTION'
  | 'EVALUATION_ERROR_ANALYSIS_AND_TUNING'
  | 'MULTI_AGENT_COORDINATION'
  | 'GUARDRAILS_AND_ACCOUNTABILITY'

export type DomainOption = {
  value: Domain
  label: string
}

export const DOMAINS: DomainOption[] = [
  {
    value: 'AGENT_ARCHITECTURE_AND_SDLC',
    label: 'Domain 1: Agent Architecture and SDLC Processes',
  },
  {
    value: 'TOOL_USE_AND_ENVIRONMENT',
    label: 'Domain 2: Tool Use and Environment Interaction',
  },
  {
    value: 'MEMORY_STATE_AND_EXECUTION',
    label: 'Domain 3: Memory, State, and Execution',
  },
  {
    value: 'EVALUATION_ERROR_ANALYSIS_AND_TUNING',
    label: 'Domain 4: Evaluation, Error Analysis, and Tuning',
  },
  {
    value: 'MULTI_AGENT_COORDINATION',
    label: 'Domain 5: Multi-Agent Coordination',
  },
  {
    value: 'GUARDRAILS_AND_ACCOUNTABILITY',
    label: 'Domain 6: Guardrails and Accountability',
  },
]

export type AnswerOption = {
  id: number
  text: string
  correct: boolean
  explanation: string
}

export type Question = {
  id: number
  text: string
  domain: Domain
  domainTitle: string
  explanation: string
  multiSelect: boolean
  options: AnswerOption[]
}


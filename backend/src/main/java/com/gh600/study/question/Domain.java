package com.gh600.study.question;

/**
 * The six domains of the GH-600 exam.
 *
 * Stored in the database as a VARCHAR via @Enumerated(EnumType.STRING) on Question.domain.
 * Always prefer EnumType.STRING over the default EnumType.ORDINAL (0, 1, 2...):
 * if you ever reorder or insert a new enum constant, ORDINAL silently corrupts
 * existing rows by shifting their numbers, whereas STRING stays human-readable and safe.
 */
public enum Domain {
    AGENT_ARCHITECTURE_AND_SDLC("Domain 1: Agent Architecture and SDLC Processes"),
    TOOL_USE_AND_ENVIRONMENT("Domain 2: Tool Use and Environment Interaction"),
    MEMORY_STATE_AND_EXECUTION("Domain 3: Memory, State, and Execution"),
    EVALUATION_ERROR_ANALYSIS_AND_TUNING("Domain 4: Evaluation, Error Analysis, and Tuning"),
    MULTI_AGENT_COORDINATION("Domain 5: Multi-Agent Coordination"),
    GUARDRAILS_AND_ACCOUNTABILITY("Domain 6: Guardrails and Accountability");

    private final String title;

    Domain(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}


package com.gh600.study.question;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds sample GH-600 questions into H2 on startup if the database is empty.
 *
 * Any @Component implementing CommandLineRunner runs automatically once the
 * Spring application context has started. Because our H2 database is stored on
 * disk (./data/gh600), we check questionRepository.count() > 0 first so restarts
 * don't insert duplicate questions.
 */
@Component
public class QuestionDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(QuestionDataSeeder.class);

    private final QuestionRepository questionRepository;

    public QuestionDataSeeder(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        long existingCount = questionRepository.count();
        if (existingCount > 0) {
            log.info("Database already contains {} questions; skipping seed.", existingCount);
            return;
        }

        // Original sample questions written for this public lab repo.
        Question q1 = new Question(
                "A team wants an agent to plan a risky database migration, and no file may change until the plan is approved. Which setup keeps planning separate from execution?",
                Domain.AGENT_ARCHITECTURE_AND_SDLC,
                "Plan-first is enforced by capability: the planner cannot write, and write tools appear only after an approved handoff.",
                false
        )
                .addOption(new AnswerOption(
                        "Give the planning stage read-only tools and enable a separate, scoped executor only after the plan is approved.",
                        true,
                        "The planner physically cannot change files, and execution starts only through an explicit, approved handoff."
                ))
                .addOption(new AnswerOption(
                        "Use one write-enabled agent and tell it in the prompt to wait for approval.",
                        false,
                        "A prompt instruction is guidance; it does not remove the write capability."
                ))
                .addOption(new AnswerOption(
                        "Let the agent commit freely and rely on review before merge.",
                        false,
                        "Merge review protects main, but files have already changed before the plan was approved."
                ))
                .addOption(new AnswerOption(
                        "Let the agent decide when its own plan is good enough to start.",
                        false,
                        "The agent's self-assessment is not an approval."
                ));

        Question q2 = new Question(
                "A scheduled workflow reads the repository's code and posts one summary issue. Which GITHUB_TOKEN permissions follow least privilege?",
                Domain.TOOL_USE_AND_ENVIRONMENT,
                "Derive permissions from what the job reads and the one thing it writes.",
                false
        )
                .addOption(new AnswerOption(
                        "contents: read and issues: write",
                        true,
                        "Read access for the input and write access only for the single output."
                ))
                .addOption(new AnswerOption(
                        "write-all",
                        false,
                        "Grants far more than the job needs."
                ))
                .addOption(new AnswerOption(
                        "contents: read only",
                        false,
                        "Too little: the job could not create the issue."
                ))
                .addOption(new AnswerOption(
                        "An organization owner's personal access token",
                        false,
                        "Over-privileged, tied to one person, and hard to audit."
                ));

        Question q3 = new Question(
                "Changes to .github/workflows/ must not merge without platform-team approval. Which two settings together enforce this? (Select two.)",
                Domain.GUARDRAILS_AND_ACCOUNTABILITY,
                "CODEOWNERS routes the review; a ruleset that requires code-owner review turns it into a merge gate.",
                true
        )
                .addOption(new AnswerOption(
                        "Assign .github/workflows/ to the platform team in CODEOWNERS.",
                        true,
                        "Routes the review of workflow changes to the right owners."
                ))
                .addOption(new AnswerOption(
                        "Require code owner review in the ruleset for the default branch.",
                        true,
                        "Makes the owners' approval a condition for merging."
                ))
                .addOption(new AnswerOption(
                        "Add a needs-platform-review label to agent pull requests.",
                        false,
                        "A label is advisory and blocks nothing."
                ))
                .addOption(new AnswerOption(
                        "Add a reminder to the pull request template.",
                        false,
                        "A template shapes the description but enforces nothing."
                ));

        questionRepository.saveAll(List.of(q1, q2, q3));
        log.info("Seeded {} sample GH-600 questions into H2.", questionRepository.count());
    }
}


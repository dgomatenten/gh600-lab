package com.gh600.study.question;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

/**
 * @DataJpaTest is a "slice test" for the persistence layer:
 *   - Configures an in-memory H2 database just for the test
 *   - Scans @Entity classes and Spring Data @Repository interfaces
 *   - Does NOT start the web server or regular @Components (like QuestionDataSeeder)
 *   - Rolls back each @Test transaction automatically so tests stay isolated
 */
@DataJpaTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Question sampleQuestion(Domain domain, boolean multiSelect) {
        return new Question(
                "Sample question text for " + domain,
                domain,
                "Sample overall explanation",
                multiSelect
        )
                .addOption(new AnswerOption("Option A", true, "Why A is right"))
                .addOption(new AnswerOption("Option B", false, "Why B is wrong"));
    }

    @Test
    void savesQuestionAndCascadesAnswerOptions() {
        Question saved = questionRepository.save(sampleQuestion(Domain.TOOL_USE_AND_ENVIRONMENT, false));
        entityManager.flush();
        entityManager.clear(); // clear 1st-level cache so findById actually queries H2

        Question found = questionRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getDomain()).isEqualTo(Domain.TOOL_USE_AND_ENVIRONMENT);
        assertThat(found.getOptions()).hasSize(2);
        assertThat(found.getOptions().get(0).getText()).isEqualTo("Option A");
        assertThat(found.getOptions().get(0).isCorrect()).isTrue();
    }

    @Test
    void filtersByDomainAndMultiSelect() {
        questionRepository.saveAll(List.of(
                sampleQuestion(Domain.AGENT_ARCHITECTURE_AND_SDLC, false),
                sampleQuestion(Domain.AGENT_ARCHITECTURE_AND_SDLC, true),
                sampleQuestion(Domain.GUARDRAILS_AND_ACCOUNTABILITY, false)
        ));

        List<Question> domain1 = questionRepository.findByDomain(Domain.AGENT_ARCHITECTURE_AND_SDLC);
        assertThat(domain1).hasSize(2);

        List<Question> multi = questionRepository.findByMultiSelectTrue();
        assertThat(multi).hasSize(1);
    }

    @Test
    void joinFetchLoadsOptionsWithoutLazyInitializationError() {
        questionRepository.save(sampleQuestion(Domain.MEMORY_STATE_AND_EXECUTION, false));
        entityManager.flush();
        entityManager.clear();

        List<Question> results = questionRepository.findByDomainWithOptions(Domain.MEMORY_STATE_AND_EXECUTION);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getOptions()).hasSize(2);
    }

    @Test
    void orphanRemovalDeletesRemovedOption() {
        Question saved = questionRepository.save(sampleQuestion(Domain.MULTI_AGENT_COORDINATION, false));
        entityManager.flush();

        saved.removeOption(saved.getOptions().get(1));
        entityManager.flush();
        entityManager.clear();

        Question reloaded = questionRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getOptions()).hasSize(1);
        assertThat(reloaded.getOptions().get(0).getText()).isEqualTo("Option A");
    }
}


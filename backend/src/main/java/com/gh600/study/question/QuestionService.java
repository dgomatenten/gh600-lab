package com.gh600.study.question;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic layer for managing GH-600 questions.
 *
 * Why a Service layer between Controller and Repository?
 *   1. Transactions: @Transactional ensures that saving a Question + its AnswerOptions
 *      (or updating them) happens atomically — either all SQL statements succeed or all roll back.
 *   2. DTO mapping inside the transaction: converting Question -> QuestionDto inside
 *      @Transactional guarantees the Hibernate session is still open when options are read.
 *   3. Separation of concerns: the Controller only cares about HTTP (status codes, headers),
 *      while the Service handles domain rules and persistence orchestration.
 */
@Service
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * Constructor injection (preferred over field @Autowired):
     *   - Makes dependencies explicit and immutable (final field)
     *   - Easy to instantiate in plain JUnit tests with a mock or fake repository
     *   - When a class has only one constructor, Spring injects it automatically without @Autowired
     */
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<QuestionDto> findAll(Domain domain) {
        List<Question> questions = (domain == null)
                ? questionRepository.findAllWithOptions()
                : questionRepository.findByDomainWithOptions(domain);
        return questions.stream()
                .map(QuestionDto::from)
                .toList();
    }

    public QuestionDto findById(Long id) {
        Question question = questionRepository.findByIdWithOptions(id)
                .orElseThrow(() -> new NotFoundException("Question not found with id: " + id));
        return QuestionDto.from(question);
    }

    @Transactional
    public QuestionDto create(CreateQuestionRequest request) {
        Question question = new Question(
                request.text(),
                request.domain(),
                request.explanation(),
                request.multiSelect()
        );
        for (CreateAnswerOptionRequest opt : request.options()) {
            question.addOption(new AnswerOption(opt.text(), opt.correct(), opt.explanation()));
        }
        Question saved = questionRepository.save(question);
        return QuestionDto.from(saved);
    }

    @Transactional
    public QuestionDto update(Long id, CreateQuestionRequest request) {
        Question question = questionRepository.findByIdWithOptions(id)
                .orElseThrow(() -> new NotFoundException("Question not found with id: " + id));

        question.setText(request.text());
        question.setDomain(request.domain());
        question.setExplanation(request.explanation());
        question.setMultiSelect(request.multiSelect());

        // Clear existing options (orphanRemoval = true deletes their rows) and add the updated ones
        question.getOptions().clear();
        for (CreateAnswerOptionRequest opt : request.options()) {
            question.addOption(new AnswerOption(opt.text(), opt.correct(), opt.explanation()));
        }

        Question saved = questionRepository.save(question);
        return QuestionDto.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new NotFoundException("Question not found with id: " + id);
        }
        questionRepository.deleteById(id);
    }
}


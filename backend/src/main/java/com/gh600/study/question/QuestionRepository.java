package com.gh600.study.question;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA Repository for Question entities.
 *
 * You only declare the interface — Spring generates the implementation class at startup!
 * Extending JpaRepository<Question, Long> gives you CRUD methods for free:
 *   - save(question), saveAll(questions)
 *   - findById(id), findAll(), count(), existsById(id)
 *   - deleteById(id), delete(question)
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Derived query method: Spring parses the method name "findByDomain" and automatically
     * generates: SELECT q FROM Question q WHERE q.domain = ?1
     */
    List<Question> findByDomain(Domain domain);

    /**
     * Another derived query: finds all multi-select ("Select all that apply") questions.
     * Generates: SELECT q FROM Question q WHERE q.multiSelect = true
     */
    List<Question> findByMultiSelectTrue();

    /**
     * Custom JPQL query using LEFT JOIN FETCH to solve the "N+1 query problem".
     *
     * Without JOIN FETCH:
     *   1 query loads N questions -> then accessing q.getOptions() on each question
     *   fires N more queries (1 + N queries total!).
     *
     * With LEFT JOIN FETCH:
     *   Hibernate loads both Question and its AnswerOptions in a SINGLE SQL SELECT with a JOIN.
     */
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.options")
    List<Question> findAllWithOptions();

    /**
     * Loads questions for a specific domain together with their options in a single SQL query.
     */
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.options WHERE q.domain = :domain")
    List<Question> findByDomainWithOptions(@Param("domain") Domain domain);

    /**
     * Loads a single question by ID together with its options in a single SQL query.
     */
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.options WHERE q.id = :id")
    Optional<Question> findByIdWithOptions(@Param("id") Long id);
}


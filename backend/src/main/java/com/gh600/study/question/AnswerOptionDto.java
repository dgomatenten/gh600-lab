package com.gh600.study.question;

/**
 * Outbound DTO (Data Transfer Object) for a single answer choice.
 *
 * Why return a DTO record instead of the JPA @Entity (AnswerOption) directly?
 *   1. No infinite JSON recursion: AnswerOption -> Question -> List<AnswerOption> -> ...
 *   2. No LazyInitializationException: Jackson won't accidentally touch uninitialized
 *      Hibernate proxies after the DB session has closed.
 *   3. Decoupling: you can reshape the database tables without breaking the JSON contract.
 */
public record AnswerOptionDto(
        Long id,
        String text,
        boolean correct,
        String explanation
) {
    public static AnswerOptionDto from(AnswerOption option) {
        return new AnswerOptionDto(
                option.getId(),
                option.getText(),
                option.isCorrect(),
                option.getExplanation()
        );
    }
}


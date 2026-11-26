package com.gh600.study.question;

import java.util.List;

/**
 * Outbound DTO representing a Question and its AnswerOptions as JSON.
 */
public record QuestionDto(
        Long id,
        String text,
        Domain domain,
        String domainTitle,
        String explanation,
        boolean multiSelect,
        List<AnswerOptionDto> options
) {
    public static QuestionDto from(Question question) {
        List<AnswerOptionDto> optionDtos = question.getOptions().stream()
                .map(AnswerOptionDto::from)
                .toList();
        return new QuestionDto(
                question.getId(),
                question.getText(),
                question.getDomain(),
                question.getDomain().getTitle(),
                question.getExplanation(),
                question.isMultiSelect(),
                optionDtos
        );
    }
}


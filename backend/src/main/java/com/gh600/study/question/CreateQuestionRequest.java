package com.gh600.study.question;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Inbound request payload for POST /api/questions and PUT /api/questions/{id}.
 *
 * Notice @Valid inside List<@Valid CreateAnswerOptionRequest>: that tells Jakarta
 * Validation to also validate every element inside the options list!
 */
public record CreateQuestionRequest(
        @NotBlank(message = "Question text must not be blank")
        @Size(max = 2000, message = "Question text must be at most 2000 characters")
        String text,

        @NotNull(message = "Domain is required")
        Domain domain,

        @NotBlank(message = "Explanation must not be blank")
        @Size(max = 2000, message = "Explanation must be at most 2000 characters")
        String explanation,

        boolean multiSelect,

        @NotNull(message = "Options list is required")
        @Size(min = 2, max = 10, message = "A question must have between 2 and 10 options")
        List<@Valid CreateAnswerOptionRequest> options
) {
}


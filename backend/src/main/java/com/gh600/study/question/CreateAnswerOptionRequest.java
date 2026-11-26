package com.gh600.study.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Inbound request payload for one answer option when creating or updating a question.
 *
 * Bean Validation annotations (@NotBlank, @Size) are checked automatically by Spring
 * when the controller method parameter is annotated with @Valid.
 */
public record CreateAnswerOptionRequest(
        @NotBlank(message = "Option text must not be blank")
        @Size(max = 1000, message = "Option text must be at most 1000 characters")
        String text,

        boolean correct,

        @NotBlank(message = "Option explanation must not be blank")
        @Size(max = 1000, message = "Option explanation must be at most 1000 characters")
        String explanation
) {
}


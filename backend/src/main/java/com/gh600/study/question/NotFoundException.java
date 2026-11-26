package com.gh600.study.question;

/**
 * Thrown when a requested resource (such as a Question by ID) does not exist.
 * Caught by ApiExceptionHandler and turned into an HTTP 404 ProblemDetail response.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}


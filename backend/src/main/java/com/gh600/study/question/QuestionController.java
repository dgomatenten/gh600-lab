package com.gh600.study.question;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * REST API controller for GH-600 study questions.
 *
 * Routes under /api/questions:
 *   - GET    /api/questions[?domain=...] -> 200 OK + List<QuestionDto>
 *   - GET    /api/questions/{id}         -> 200 OK + QuestionDto (or 404 ProblemDetail)
 *   - POST   /api/questions              -> 201 Created + Location header + QuestionDto
 *   - PUT    /api/questions/{id}         -> 200 OK + QuestionDto (or 404 ProblemDetail)
 *   - DELETE /api/questions/{id}         -> 204 No Content (or 404 ProblemDetail)
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public List<QuestionDto> list(@RequestParam(required = false) Domain domain) {
        return questionService.findAll(domain);
    }

    @GetMapping("/{id}")
    public QuestionDto getById(@PathVariable Long id) {
        return questionService.findById(id);
    }

    @PostMapping
    public ResponseEntity<QuestionDto> create(@Valid @RequestBody CreateQuestionRequest request) {
        QuestionDto created = questionService.create(request);
        URI location = URI.create("/api/questions/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public QuestionDto update(
            @PathVariable Long id,
            @Valid @RequestBody CreateQuestionRequest request
    ) {
        return questionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


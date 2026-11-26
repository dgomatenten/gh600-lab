package com.gh600.study.question;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web-slice unit tests for QuestionController and ApiExceptionHandler.
 *
 * @WebMvcTest(QuestionController.class) loads only the web layer (controller, validation,
 * exception handler, Jackson JSON converter) — no database or JPA repositories.
 * @MockitoBean replaces QuestionService in the Spring test context with a Mockito mock.
 */
@WebMvcTest(QuestionController.class)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService questionService;

    private QuestionDto sampleDto(Long id, Domain domain) {
        return new QuestionDto(
                id,
                "Sample GH-600 question?",
                domain,
                domain.getTitle(),
                "Overall explanation",
                false,
                List.of(
                        new AnswerOptionDto(1L, "Option A", true, "Right"),
                        new AnswerOptionDto(2L, "Option B", false, "Wrong")
                )
        );
    }

    @Test
    void listsQuestions() throws Exception {
        given(questionService.findAll(null))
                .willReturn(List.of(sampleDto(1L, Domain.AGENT_ARCHITECTURE_AND_SDLC)));

        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].domain").value("AGENT_ARCHITECTURE_AND_SDLC"))
                .andExpect(jsonPath("$[0].options.length()").value(2));
    }

    @Test
    void filtersQuestionsByDomain() throws Exception {
        given(questionService.findAll(Domain.GUARDRAILS_AND_ACCOUNTABILITY))
                .willReturn(List.of(sampleDto(3L, Domain.GUARDRAILS_AND_ACCOUNTABILITY)));

        mockMvc.perform(get("/api/questions").param("domain", "GUARDRAILS_AND_ACCOUNTABILITY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].domain").value("GUARDRAILS_AND_ACCOUNTABILITY"));
    }

    @Test
    void returns404ProblemDetailWhenNotFound() throws Exception {
        given(questionService.findById(999L))
                .willThrow(new NotFoundException("Question not found with id: 999"));

        mockMvc.perform(get("/api/questions/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Question not found with id: 999"));
    }

    @Test
    void createsValidQuestionAndReturns201WithLocation() throws Exception {
        given(questionService.create(any(CreateQuestionRequest.class)))
                .willReturn(sampleDto(10L, Domain.TOOL_USE_AND_ENVIRONMENT));

        String requestJson = """
                {
                  "text": "Sample GH-600 question?",
                  "domain": "TOOL_USE_AND_ENVIRONMENT",
                  "explanation": "Overall explanation",
                  "multiSelect": false,
                  "options": [
                    { "text": "Option A", "correct": true, "explanation": "Right" },
                    { "text": "Option B", "correct": false, "explanation": "Wrong" }
                  ]
                }
                """;

        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/questions/10"))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void rejectsInvalidCreateRequestWith400ValidationErrors() throws Exception {
        String invalidJson = """
                {
                  "text": "",
                  "domain": null,
                  "explanation": "",
                  "multiSelect": false,
                  "options": []
                }
                """;

        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors.text").exists())
                .andExpect(jsonPath("$.errors.domain").exists())
                .andExpect(jsonPath("$.errors.options").exists());
    }

    @Test
    void deletesQuestionAndReturns204() throws Exception {
        mockMvc.perform(delete("/api/questions/1"))
                .andExpect(status().isNoContent());

        verify(questionService).delete(1L);
    }

    @Test
    void deleteReturns404WhenQuestionDoesNotExist() throws Exception {
        doThrow(new NotFoundException("Question not found with id: 404"))
                .when(questionService).delete(404L);

        mockMvc.perform(delete("/api/questions/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}


package com.gh600.study;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @WebMvcTest starts ONLY the web layer (controllers, JSON conversion) - no database,
 * so it's fast. MockMvc sends fake HTTP requests without opening a real port.
 */
@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired // Spring "injects" (hands us) a ready-made MockMvc object.
    private MockMvc mockMvc;

    @Test
    void greetsByName() throws Exception {
        mockMvc.perform(get("/api/hello").param("name", "Ruru"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, Ruru!"));
    }

    @Test
    void usesDefaultName() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, GH-600 learner!"));
    }
}

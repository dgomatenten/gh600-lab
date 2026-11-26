package com.gh600.study;

import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Your first REST endpoint.
 *
 * @RestController = @Controller + @ResponseBody:
 *   - @Controller tells Spring "create one instance of this class and route HTTP requests to it"
 *     (Spring finds it automatically because it lives under the package of BackendApplication).
 *   - @ResponseBody means return values are written straight into the HTTP response,
 *     converted to JSON by the Jackson library (no HTML templates involved).
 *
 * @RequestMapping("/api") prefixes every route in this class with /api.
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    /**
     * A Java "record" is an immutable data class: Java generates the constructor,
     * getters (message(), serverTime()), equals/hashCode and toString for you.
     * Jackson turns it into: {"message":"...","serverTime":"..."}
     *
     * Records are perfect for DTOs (Data Transfer Objects) - the shapes you send over HTTP.
     */
    public record HelloResponse(String message, Instant serverTime) {}

    /**
     * GET /api/hello            -> {"message":"Hello, GH-600 learner!", ...}
     * GET /api/hello?name=Ruru  -> {"message":"Hello, Ruru!", ...}
     *
     * @RequestParam binds the ?name= query parameter to the method argument.
     */
    @GetMapping("/hello")
    public HelloResponse hello(@RequestParam(defaultValue = "GH-600 learner") String name) {
        return new HelloResponse("Hello, " + name + "!", Instant.now());
    }
}

package com.nitsher01.it;

import com.nitsher01.service.SemanticCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SemanticCacheIT {

    @Autowired
    private SemanticCacheService cache;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void cleanDatabase() {
        jdbc.execute("TRUNCATE TABLE llm_cache RESTART IDENTITY");
    }

    @Test
    void testCacheReturnsSameAnswerForSimilarQuestions() {
        String q1 = "What is Java?";
        String q2 = "Tell me about the Java language";
        String q3 = "What is the Java language?";
        String q4 = "I'm new to Java and I want to learn it. Explain it to me.";

        String a1 = cache.getAnswer(q1);
        String a2 = cache.getAnswer(q2);
        String a3 = cache.getAnswer(q3);
        String a4 = cache.getAnswer(q4);

        assertNotNull(a1);
        assertNotNull(a2);
        assertNotNull(a3);
        assertNotNull(a4);

        a2 = a2.replace("(cached) )", "");
        a3 = a3.replace("(cached) )", "");
        a4 = a4.replace("(cached) )", "");

        assertEquals(a1, a2, "Expected cache to serve the second response");
        assertEquals(a1, a3, "Expected cache to serve the third response");
        assertNotEquals(a1, a4, "Expected to fetch the fourth response from LLM. Depending of the configured threshold this can change");
    }
}
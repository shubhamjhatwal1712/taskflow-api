package com.shubham.taskflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("demo")
class TaskFlowApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring application context (controllers, services,
        // repositories, and the H2 datasource) wires up correctly.
    }
}

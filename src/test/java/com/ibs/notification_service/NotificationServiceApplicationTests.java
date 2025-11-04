package com.ibs.notification_service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;
@SpringBootTest
class NotificationServiceApplicationTest {

    @Test
    void contextLoads() {
        // Test to ensure Spring context loads successfully
    }

    @Test
    void main_shouldRunApplicationSuccessfully() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            // Run the main method
            NotificationServiceApplication.main(new String[]{});

            // Verify that SpringApplication.run() was called
            mocked.verify(() -> SpringApplication.run(NotificationServiceApplication.class, new String[]{}));
        }
    }
}

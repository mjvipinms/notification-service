package com.ibs.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibs.notification_service.events.InterviewCreatedEvent;
import com.ibs.notification_service.events.NotificationEvent;
import com.ibs.notification_service.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final EmailNotificationService emailNotificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "interview-events", groupId = "notification-service-group")
    public void consume(NotificationEvent event) {
        try {
            log.info("Received NotificationEvent: {}", event);

            switch (event.getEventType()) {
                case "INTERVIEWCREATED" -> {
                    InterviewCreatedEvent createdEvent = mapTo(event.getPayload(), InterviewCreatedEvent.class);
                    handleInterviewCreated(createdEvent);
                }
                // You can add more cases later
                // case "INTERVIEWUPDATED" -> handleInterviewUpdated(mapTo(event.getPayload(), InterviewUpdatedEvent.class));

                default -> log.warn(" Unknown event type received: {}", event.getEventType());
            }

        } catch (Exception e) {
            log.error(" Exception while processing event: {}", e.getMessage(), e);
        }
    }

    private void handleInterviewCreated(InterviewCreatedEvent event) {
        log.info("Handling InterviewCreatedEvent for Interview ID: {}", event.getInterviewId());
        emailNotificationService.sendInterviewCreatedMail(event);
    }

    private <T> T mapTo(Object data, Class<T> targetType) {
        return objectMapper.convertValue(data, targetType);
    }
}

package com.ibs.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibs.notification_service.events.InterviewCreatedEvent;
import com.ibs.notification_service.events.NotificationEvent;
import com.ibs.notification_service.service.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class NotificationEventListenerTest {

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    private InterviewCreatedEvent mockInterviewEvent;
    private NotificationEvent notificationEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockInterviewEvent = InterviewCreatedEvent.builder()
                .interviewId(1001)
                .candidateEmail("candidate@test.com")
                .panelEmail("panel@test.com")
                .hrEmail("hr@test.com")
                .build();
    }

    @Test
    void consume_shouldHandleInterviewCreatedEvent() {
        notificationEvent = NotificationEvent.builder()
                .eventType("INTERVIEWCREATED")
                .payload(mockInterviewEvent)
                .build();

        when(objectMapper.convertValue(mockInterviewEvent, InterviewCreatedEvent.class))
                .thenReturn(mockInterviewEvent);

        notificationEventListener.consume(notificationEvent);

        verify(emailNotificationService, times(1)).sendInterviewCreatedMail(mockInterviewEvent);
        verify(emailNotificationService, never()).sendInterviewUpdateMail(any());
        verify(emailNotificationService, never()).sendInterviewRescheduledMail(any());
    }

    @Test
    void consume_shouldHandleInterviewUpdatedEvent() {
        notificationEvent = NotificationEvent.builder()
                .eventType("INTERVIEWUPDATED")
                .payload(mockInterviewEvent)
                .build();

        when(objectMapper.convertValue(mockInterviewEvent, InterviewCreatedEvent.class))
                .thenReturn(mockInterviewEvent);

        notificationEventListener.consume(notificationEvent);

        verify(emailNotificationService, times(1)).sendInterviewUpdateMail(mockInterviewEvent);
    }

    @Test
    void consume_shouldHandleInterviewRescheduleEvent() {
        notificationEvent = NotificationEvent.builder()
                .eventType("INTERVIEWRESCHEDULE")
                .payload(mockInterviewEvent)
                .build();

        when(objectMapper.convertValue(mockInterviewEvent, InterviewCreatedEvent.class))
                .thenReturn(mockInterviewEvent);

        notificationEventListener.consume(notificationEvent);

        verify(emailNotificationService, times(1)).sendInterviewRescheduledMail(mockInterviewEvent);
    }

    @Test
    void consume_shouldHandleUnknownEventTypeGracefully() {
        notificationEvent = NotificationEvent.builder()
                .eventType("UNKNOWN_EVENT")
                .payload(mockInterviewEvent)
                .build();

        notificationEventListener.consume(notificationEvent);

        // Verify that no email methods are triggered
        verifyNoInteractions(emailNotificationService);
    }

    @Test
    void consume_shouldHandleExceptionGracefully() {
        notificationEvent = NotificationEvent.builder()
                .eventType("INTERVIEWCREATED")
                .payload(mockInterviewEvent)
                .build();

        when(objectMapper.convertValue(any(), eq(InterviewCreatedEvent.class)))
                .thenThrow(new RuntimeException("Mapping failed"));

        notificationEventListener.consume(notificationEvent);

        verifyNoInteractions(emailNotificationService);
    }
}

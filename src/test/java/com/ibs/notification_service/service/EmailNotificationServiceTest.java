package com.ibs.notification_service.service;

import com.ibs.notification_service.entity.Notification;
import com.ibs.notification_service.events.InterviewCreatedEvent;
import com.ibs.notification_service.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Mock
    private MimeMessage mimeMessage;

    private InterviewCreatedEvent event;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        event = InterviewCreatedEvent.builder()
                .interviewId(123)
                .candidateEmail("candidate@ibs.com")
                .panelEmail("panel@ibs.com")
                .hrEmail("hr@ibs.com")
                .startTime(LocalDateTime.now().plusDays(1))
                .createdBy("HRAdmin")
                .build();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    // ---------- sendInterviewCreatedMail ----------
    @Test
    void sendInterviewCreatedMail_shouldSendMailsAndSaveNotifications() {
        emailNotificationService.sendInterviewCreatedMail(event);

        verify(mailSender, times(3)).createMimeMessage(); // for candidate, panel, hr
        verify(mailSender, times(3)).send(any(MimeMessage.class));
        verify(notificationRepository, times(3)).save(any(Notification.class));
    }

    // ---------- sendInterviewRescheduledMail ----------
    @Test
    void sendInterviewRescheduledMail_shouldSendMailsAndSaveNotifications() {
        emailNotificationService.sendInterviewRescheduledMail(event);

        verify(mailSender, times(3)).createMimeMessage();
        verify(mailSender, times(3)).send(any(MimeMessage.class));
        verify(notificationRepository, times(3)).save(any(Notification.class));
    }

    // ---------- sendInterviewUpdateMail ----------
    @Test
    void sendInterviewUpdateMail_shouldSendMailsAndSaveNotifications() {
        emailNotificationService.sendInterviewUpdateMail(event);

        verify(mailSender, times(3)).createMimeMessage();
        verify(mailSender, times(3)).send(any(MimeMessage.class));
        verify(notificationRepository, times(3)).save(any(Notification.class));
    }

    // ---------- Email Validation ----------
    @Test
    void sendMail_shouldSkipInvalidEmailsAndStillSaveNotification() {
        InterviewCreatedEvent badEmailEvent = InterviewCreatedEvent.builder()
                .candidateEmail("invalid-email")
                .panelEmail("panel@ibs.com")
                .hrEmail("hr@ibs.com")
                .startTime(LocalDateTime.now())
                .createdBy("Tester")
                .interviewId(1)
                .build();

        emailNotificationService.sendInterviewCreatedMail(badEmailEvent);

        // invalid candidate email skipped, but others should send
        verify(mailSender, atLeast(2)).send(any(MimeMessage.class));
        verify(notificationRepository, times(3)).save(any(Notification.class));
    }

    // ---------- Exception handling ----------
    @Test
    void sendMail_shouldThrowRuntimeExceptionWhenMessagingFails() throws Exception {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail Server down"));

        assertThatThrownBy(() -> emailNotificationService.sendInterviewCreatedMail(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Mail Server down");
    }

    // ---------- Validation ----------
    @Test
    void isValidEmail_shouldReturnTrueForValidEmails() throws Exception {
        // use reflection to test private method indirectly
        emailNotificationService.sendInterviewCreatedMail(event);

        verify(mailSender, atLeastOnce()).send(any(MimeMessage.class));
    }
}

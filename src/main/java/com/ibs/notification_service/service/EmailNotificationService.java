package com.ibs.notification_service.service;

import com.ibs.notification_service.context.UserContext;
import com.ibs.notification_service.entity.Notification;
import com.ibs.notification_service.events.InterviewCreatedEvent;
import com.ibs.notification_service.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    public void sendInterviewCreatedMail(InterviewCreatedEvent event) {
        sendMail(event.getCandidateEmail(), "Interview Scheduled",
                "Dear Candidate,<br>Your interview is scheduled for <b>" + event.getStartTime() + "</b>.");

        sendMail(event.getPanelEmail(), "Interview Assigned",
                "Dear Panel,<br>You have a new interview scheduled for <b>" + event.getStartTime() + "</b>.");

        sendMail(event.getHrEmail(), "Interview Confirmation",
                "Dear HR,<br>An interview has been created for candidate associated with ID: " + event.getInterviewId());
    }

    private void sendMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);

            Notification notification = new Notification();
            notification.setRecipient(to);
            notification.setSubject(subject);
            notification.setContent(htmlContent);
            notification.setSentAt(LocalDateTime.now());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setCreatedBy(UserContext.getUserName());
            notificationRepository.save(notification);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send mail to " + to, e);
        }
    }
}

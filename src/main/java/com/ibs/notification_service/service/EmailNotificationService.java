package com.ibs.notification_service.service;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    /**
     *
     * @param event to be sent
     */
    public void sendInterviewCreatedMail(InterviewCreatedEvent event) {
        sendMail(event.getCandidateEmail(), "Interview Scheduled",
                "Dear Candidate,<br>Your interview is scheduled for <b>" + event.getStartTime()  +
                        "<br>Thanks & Regards," +
                        "<br>IBS Talent Team", event.getCreatedBy());

        sendMail(event.getPanelEmail(), "Interview Assigned",
                "Dear Panel,<br>You have a new interview scheduled for <b>" + event.getStartTime()  +
                        "<br>Thanks & Regards," +
                        "<br>IBS Talent Team", event.getCreatedBy());

        sendMail(event.getHrEmail(), "Interview Confirmation",
                "Dear HR,<br>An interview has been created for candidate associated with ID: " + event.getInterviewId() +
                        "<br>Thanks & Regards," +
                        "<br>IBS Talent Team", event.getCreatedBy());
    }

    /**
     *
     * @param event to be sent
     */
    public void sendInterviewRescheduledMail(InterviewCreatedEvent event) {
        sendMail(event.getCandidateEmail(), "Interview Rescheduled",
                "Dear Candidate,<br>Your interview is scheduled for <b>" + event.getStartTime() + "</b>." +
                        "<br>Thanks & Regards," +
                        "<br>IBS Talent Team", event.getCreatedBy());

        sendMail(event.getPanelEmail(), "Interview Assigned",
                "Dear Panel,<br>You have a new interview rescheduled for <b>" + event.getStartTime() + "</b>." +
                        "<br>Thanks & Regards," +
                        "<br>IBS Talent Team", event.getCreatedBy());

        sendMail(event.getHrEmail(), "Interview Confirmation",
                "Dear HR,<br>An interview has been rescheduled for candidate associated with ID: <b>"
                        + event.getInterviewId() + "</b>."
                        + "<br><br>Thanks & Regards,"
                        + "<br><strong>IBS Talent Team</strong>", event.getCreatedBy()
        );

    }

    /**
     *
     * @param to          recipient email
     * @param subject     subject of the mail
     * @param htmlContent content
     */
    private void sendMail(String to, String subject, String htmlContent, String createdBy) {
        try {

            if (isValidEmail(to) && !to.contains("test")) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                mailSender.send(message);
            }
            Notification notification = new Notification();
            notification.setRecipient(to);
            notification.setSubject(subject);
            notification.setContent(htmlContent);
            notification.setSentAt(LocalDateTime.now());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setCreatedBy(createdBy);
            notificationRepository.save(notification);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send mail to " + to, e);
        }
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        Matcher matcher = EMAIL_PATTERN.matcher(email.trim());
        return matcher.matches();
    }
}

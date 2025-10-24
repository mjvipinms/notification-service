package com.ibs.notification_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;
    private String recipient;
    private String subject;
    @Column(length = 1000)
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
    private String createdBy;
    private String updatedBy;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

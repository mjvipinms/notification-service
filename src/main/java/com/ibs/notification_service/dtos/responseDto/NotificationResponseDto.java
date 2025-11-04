package com.ibs.notification_service.dtos.responseDto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Integer notificationId;
    private String recipient;
    private String subject;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
package com.ibs.notification_service.dtos.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {
    private Integer notificationId;
    private Boolean isRead;
    private String userId;
    private int page = 0;
    private int size = 10;
}

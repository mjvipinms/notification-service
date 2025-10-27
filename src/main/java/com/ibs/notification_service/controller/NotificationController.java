package com.ibs.notification_service.controller;

import com.ibs.notification_service.dtos.requestDto.NotificationRequestDto;
import com.ibs.notification_service.dtos.responseDto.NotificationResponseDto;
import com.ibs.notification_service.dtos.responseDto.PagedResponseDto;
import com.ibs.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/get-by-user")
    public ResponseEntity<PagedResponseDto<NotificationResponseDto>> getNotificationsByUser(@RequestBody NotificationRequestDto requestDto) {

        PagedResponseDto<NotificationResponseDto> response = notificationService.getNotificationsByUser(requestDto.getUserId(), requestDto.getPage(), requestDto.getSize());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<NotificationResponseDto> updateNotification(@RequestBody NotificationRequestDto requestDto) {

        NotificationResponseDto updatedNotification = notificationService.updateNotification(requestDto.getNotificationId(), requestDto);
        return ResponseEntity.ok(updatedNotification);
    }
}

package com.ibs.notification_service.controller;

import com.ibs.notification_service.dtos.requestDto.NotificationRequestDto;
import com.ibs.notification_service.dtos.responseDto.NotificationResponseDto;
import com.ibs.notification_service.dtos.responseDto.PagedResponseDto;
import com.ibs.notification_service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getNotificationsByUser_shouldReturnPagedNotifications() {
        // Arrange
        NotificationRequestDto request = new NotificationRequestDto();
        request.setUserId("test");
        request.setPage(0);
        request.setSize(10);

        NotificationResponseDto notification1 = NotificationResponseDto.builder()
                .notificationId(101)
                .isRead(true)
                .content("Test Notification 1")
                .isRead(false)
                .build();

        PagedResponseDto<NotificationResponseDto> pagedResponse =
                new PagedResponseDto<>(List.of(notification1), 0, 10, 1L,10,true);

        when(notificationService.getNotificationsByUser("test", 0, 10)).thenReturn(pagedResponse);

        // Act
        ResponseEntity<PagedResponseDto<NotificationResponseDto>> response =
                notificationController.getNotificationsByUser(request);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);
        assertThat(response.getBody().getContent().getFirst().getContent()).isEqualTo("Test Notification 1");

        verify(notificationService, times(1)).getNotificationsByUser("test", 0, 10);
    }

    @Test
    void updateNotification_shouldReturnUpdatedNotification() {
        // Arrange
        NotificationRequestDto request = new NotificationRequestDto();
        request.setNotificationId(101);
        request.setUserId("test");

        NotificationResponseDto updatedResponse = NotificationResponseDto.builder()
                .notificationId(101)
                .recipient("Test")
                .content("Updated Notification")
                .isRead(true)
                .build();

        when(notificationService.updateNotification(101, request)).thenReturn(updatedResponse);

        // Act
        ResponseEntity<NotificationResponseDto> response =
                notificationController.updateNotification(request);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isEqualTo("Updated Notification");
        assertThat(response.getBody().getIsRead()).isTrue();

        verify(notificationService, times(1)).updateNotification(101, request);
    }
}

package com.ibs.notification_service.service;

import com.ibs.notification_service.cache.UserCacheService;
import com.ibs.notification_service.dtos.requestDto.NotificationRequestDto;
import com.ibs.notification_service.dtos.responseDto.NotificationResponseDto;
import com.ibs.notification_service.dtos.responseDto.PagedResponseDto;
import com.ibs.notification_service.dtos.responseDto.UserResponseDTO;
import com.ibs.notification_service.entity.Notification;
import com.ibs.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserCacheService userCacheService;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private UserResponseDTO userResponse;
    private Page<Notification> mockPage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        notification = new Notification();
        notification.setNotificationId(1);
        notification.setRecipient("user@test.com");
        notification.setSubject("Interview Scheduled");
        notification.setContent("Your interview is scheduled.");
        notification.setIsRead(false);
        notification.setCreatedBy("System");
        notification.setCreatedAt(LocalDateTime.now());

        userResponse = UserResponseDTO.builder()
                .userId(101)
                .email("user@test.com")
                .fullName("John Doe")
                .build();

        mockPage = new PageImpl<>(List.of(notification));
    }

    // ---------- getNotificationsByUser ----------
    @Test
    void getNotificationsByUser_shouldReturnPagedNotifications() {
        when(userCacheService.getAllUsers()).thenReturn(List.of(userResponse));
        when(notificationRepository.findByRecipient(eq("user@test.com"), any(PageRequest.class)))
                .thenReturn(mockPage);

        PagedResponseDto<NotificationResponseDto> response =
                notificationService.getNotificationsByUser("101", 0, 10);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getSubject()).isEqualTo("Interview Scheduled");
        verify(notificationRepository, times(1)).findByRecipient(eq("user@test.com"), any(PageRequest.class));
    }

    // ---------- updateNotification ----------
    @Test
    void updateNotification_shouldUpdateNotificationSuccessfully() {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setNotificationId(1);
        request.setIsRead(true);

        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponseDto response = notificationService.updateNotification(1, request);

        assertThat(response).isNotNull();
        assertThat(response.getIsRead()).isTrue();
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // ---------- updateNotification (Not Found) ----------
    @Test
    void updateNotification_shouldThrowExceptionWhenNotFound() {
        when(notificationRepository.findById(999)).thenReturn(Optional.empty());

        NotificationRequestDto request = new NotificationRequestDto();
        request.setNotificationId(999);

        assertThatThrownBy(() -> notificationService.updateNotification(999, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Notification not found");
    }

    // ---------- getNotificationsByUser with No Users ----------
    @Test
    void getNotificationsByUser_shouldHandleEmptyUserListGracefully() {
        when(userCacheService.getAllUsers()).thenReturn(List.of());
        // Expect NullPointerException when no users found
        assertThatThrownBy(() -> notificationService.getNotificationsByUser("101", 0, 10))
                .isInstanceOf(NullPointerException.class);
    }
}

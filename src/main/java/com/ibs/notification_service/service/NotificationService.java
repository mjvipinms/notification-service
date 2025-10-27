package com.ibs.notification_service.service;

import com.ibs.notification_service.context.UserContext;
import com.ibs.notification_service.dtos.requestDto.NotificationRequestDto;
import com.ibs.notification_service.dtos.responseDto.NotificationResponseDto;
import com.ibs.notification_service.dtos.responseDto.PagedResponseDto;
import com.ibs.notification_service.entity.Notification;
import com.ibs.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     *
     * @param userId logged in userId
     * @return List<NotificationResponseDto>
     */
    public PagedResponseDto<NotificationResponseDto> getNotificationsByUser(String userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findByRecipient(userId, pageRequest);

        var content = notificationPage.getContent()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponseDto<>(
                content,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages(),
                notificationPage.isLast()
        );
    }

    /**
     *
     * @param notificationId notification entity id
     * @param request notification request
     * @return NotificationResponseDto
     */
    public NotificationResponseDto updateNotification(Integer notificationId, NotificationRequestDto request) {
       log.info("Updating notification object using notificationId");
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));

            if (request.getIsRead() != null) {
                notification.setIsRead(request.getIsRead());
            }
            notification.setUpdatedBy(UserContext.getUserName());
            notification.setUpdatedAt(LocalDateTime.now());
            Notification updatedNotification = notificationRepository.save(notification);

            return mapToDto(updatedNotification);
        } catch (RuntimeException e) {
            log.error("Exception occurred at updateNotification ,{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param notification notification entity
     * @return response entity
     */
    private NotificationResponseDto mapToDto(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setRecipient(notification.getRecipient());
        dto.setSubject(notification.getSubject());
        dto.setContent(notification.getContent());
        dto.setSentAt(notification.getSentAt());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedBy(notification.getCreatedBy());
        dto.setUpdatedBy(notification.getUpdatedBy());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());
        return dto;
    }
}

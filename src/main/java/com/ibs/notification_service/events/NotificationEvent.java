package com.ibs.notification_service.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    private String eventType; // e.g. "InterviewCreated", "InterviewUpdated"
    private Object payload;   // the actual event data
}

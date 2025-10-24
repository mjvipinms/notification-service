package com.ibs.notification_service.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InterviewCreatedEvent extends BaseEvent{
    private Integer interviewId;
    private String candidateEmail;
    private String panelEmail;
    private String hrEmail;
    private LocalDateTime startTime;
}

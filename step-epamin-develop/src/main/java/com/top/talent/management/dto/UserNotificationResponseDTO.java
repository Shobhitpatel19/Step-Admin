package com.top.talent.management.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserNotificationResponseDTO {
    private Long uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String practice;
    private List<NotificationFeatureDTO> features;
}

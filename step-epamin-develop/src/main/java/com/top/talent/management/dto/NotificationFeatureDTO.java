package com.top.talent.management.dto;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class NotificationFeatureDTO {
    private long categoryId;
    private String categoryName;
    private String description;
    private Boolean notificationsEnabled;
}

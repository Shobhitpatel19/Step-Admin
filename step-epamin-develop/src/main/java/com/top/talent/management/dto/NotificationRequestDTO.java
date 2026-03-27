package com.top.talent.management.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequestDTO {
    @NotNull(message = "User ID cannot be null")
    @Positive
    private Long userId;

    @NotNull(message = "Category ID cannot be null")
    @Positive
    private Long categoryId;

    @NotNull(message = "Enable flag cannot be null")
    private Boolean enable;
}

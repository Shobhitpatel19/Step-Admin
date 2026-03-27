package com.top.talent.management.service;

import com.top.talent.management.dto.NotificationRequestDTO;
import com.top.talent.management.dto.UserNotificationResponseDTO;

import java.util.List;

public interface NotificationManagementService {
    List<UserNotificationResponseDTO> getAllUsersWithNotificationpreferences();

    UserNotificationResponseDTO toggleAllNotificationsForUser(Long practiceHeadId, Boolean enable);

 UserNotificationResponseDTO updateCategoryNotificationStatusForUser(NotificationRequestDTO requestDTO);
}

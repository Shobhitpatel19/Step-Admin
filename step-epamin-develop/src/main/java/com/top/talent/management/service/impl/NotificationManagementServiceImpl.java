package com.top.talent.management.service.impl;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.NotificationFeatureDTO;
import com.top.talent.management.dto.NotificationRequestDTO;
import com.top.talent.management.dto.UserNotificationResponseDTO;
import com.top.talent.management.entity.EmailCategories;
import com.top.talent.management.entity.NotificationManagement;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.NotificationException;
import com.top.talent.management.exception.UserNotFoundException;
import com.top.talent.management.mapper.NotificationMapper;
import com.top.talent.management.repository.EmailCategoriesRepository;
import com.top.talent.management.repository.NotificationManagementRepository;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.NotificationManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.top.talent.management.constants.Constants.USER_STATUS_ACTIVE;
import static com.top.talent.management.constants.ErrorMessages.INVALID_NOTIFICATION_REQUEST;
import static com.top.talent.management.constants.ErrorMessages.NOTIFICATIONS_NOT_FOUND;
import static com.top.talent.management.constants.ErrorMessages.USER_NOT_FOUND_WITH_UID;
import static java.util.stream.Collectors.toList;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationManagementServiceImpl implements NotificationManagementService {

    private final UserRepository userRepository;
    private final NotificationManagementRepository notificationManagementRepository;
    private final EmailCategoriesRepository emailCategoriesRepository;
    private final NotificationMapper notificationMapper;
    private final RoleRepository roleRepository;

    @Override
    public List<UserNotificationResponseDTO> getAllUsersWithNotificationpreferences() {
        log.info("Fetching all practice heads with their features");
        Role practiceRole = roleRepository.findByName(RoleConstants.PRACTICE);
        List<User> users = userRepository.findAllByRole(practiceRole);
        List<User> activeUsers = users.stream()
                .filter(user -> USER_STATUS_ACTIVE.equalsIgnoreCase(user.getStatus()))
                .collect(Collectors.toList());
        List<EmailCategories> allCategories = emailCategoriesRepository.findAll();

        log.debug("Total user fetched: {}. Total categories fetched: {}", users.size(), allCategories.size());

        return activeUsers.stream().map(user -> {
            log.debug("Processing user: {}", user.getEmail());

            List<NotificationManagement> notifications = notificationManagementRepository.findByUserUuid(user.getUuid());

            List<NotificationManagement> missingEntries = allCategories.stream()
                    .filter(category -> notifications.stream()
                            .noneMatch(notification -> notification.getCategory().getCategoryId().equals(category.getCategoryId())))
                    .map(category -> NotificationManagement.builder()
                            .user(user)
                            .category(category)
                            .notificationsEnabled(true)
                            .created(LocalDateTime.now())
                            .createdBy(CustomUserPrincipal.getLoggedInUserEmail())
                            .lastUpdated(LocalDateTime.now())
                            .lastUpdatedBy(CustomUserPrincipal.getLoggedInUserEmail())
                            .build())
                    .collect(toList());

            if (!missingEntries.isEmpty()) {
                log.debug("Saving {} missing NotificationManagement entries for user: {}", missingEntries.size(), user.getEmail());
                notificationManagementRepository.saveAll(missingEntries);
                notifications.addAll(missingEntries);
            }
            List<NotificationFeatureDTO> features = notificationMapper.toNotificationFeatureDTOList(notifications);
            features.sort(Comparator.comparing(NotificationFeatureDTO::getCategoryId));
            return notificationMapper.toUserNotificationResponseDTO(user, features);
        }).collect(Collectors.toList());
    }

    @Override
    public UserNotificationResponseDTO toggleAllNotificationsForUser(Long userId, Boolean enable) {
        log.info("Request to toggle all notifications for user ID: {} to {}", userId, enable);

        User practiceHead = userRepository.findById(userId).filter(user -> USER_STATUS_ACTIVE.equalsIgnoreCase(user.getStatus()))
                .orElseThrow(() -> {
                    log.error("user not found with ID: {}", userId);
                    return new UserNotFoundException(USER_NOT_FOUND_WITH_UID + userId);
                });
        List<NotificationManagement> notifications = notificationManagementRepository.findByUserUuid(practiceHead.getUuid());

        if (notifications.isEmpty()) {
            log.warn("No notifications found for user with ID: {}. Skipping update.", userId);
            throw new NotificationException(NOTIFICATIONS_NOT_FOUND + userId);
        }
        notifications.forEach(notification -> {
            notification.setNotificationsEnabled(enable);
            notification.setLastUpdated(LocalDateTime.now());
            notification.setLastUpdatedBy(CustomUserPrincipal.getLoggedInUserEmail());
        });
        notificationManagementRepository.saveAll(notifications);

        List<NotificationFeatureDTO> features = notificationMapper.toNotificationFeatureDTOList(notifications);
        features.sort(Comparator.comparing(NotificationFeatureDTO::getCategoryId));

        return notificationMapper.toUserNotificationResponseDTO(practiceHead, features);}

    @Override
    public UserNotificationResponseDTO updateCategoryNotificationStatusForUser(NotificationRequestDTO requestDTO) {

        Long userId = requestDTO.getUserId();
        Long categoryId = requestDTO.getCategoryId();
        Boolean enable = requestDTO.getEnable();

        if (userId == null || categoryId == null || enable == null) {
            log.error("Invalid request: userId or categoryId is null,, or enable is null");
            throw new NotificationException(INVALID_NOTIFICATION_REQUEST );
        }
        User user = userRepository.findById(userId).filter(u -> USER_STATUS_ACTIVE.equalsIgnoreCase(u.getStatus()))
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new UserNotFoundException(USER_NOT_FOUND_WITH_UID + userId);
                });

        NotificationManagement notification = notificationManagementRepository
                .findByUserUuidAndCategoryCategoryId(user.getUuid(), categoryId)
                .orElseThrow(() -> {
                    log.error("Notification not found for user ID: {} and category ID: {}", userId, categoryId);
                    return new NotificationException(NOTIFICATIONS_NOT_FOUND + userId);
                });

        log.info("Updating notification status for category ID: {} for user ID: {} to enable: {}", categoryId, userId, enable);
        notification.setNotificationsEnabled(enable);
        notification.setLastUpdated(LocalDateTime.now());
        notification.setLastUpdatedBy(CustomUserPrincipal.getLoggedInUserEmail());
        notificationManagementRepository.save(notification);
        log.info("Notification successfully updated for category ID: {} and user ID: {}", categoryId, userId);

        List<NotificationManagement> notifications = notificationManagementRepository.findByUserUuid(user.getUuid());
        if (notifications.isEmpty()) {
            log.warn("No notifications found for User ID: {}", user.getUuid());
            throw new NotificationException(NOTIFICATIONS_NOT_FOUND + user.getUuid());
        }

        List<NotificationFeatureDTO> features = notificationMapper.toNotificationFeatureDTOList(notifications);
        features.sort(Comparator.comparing(NotificationFeatureDTO::getCategoryId));

        return notificationMapper.toUserNotificationResponseDTO(user, features);
    }}

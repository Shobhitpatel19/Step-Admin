package com.top.talent.management.service;

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
import com.top.talent.management.service.impl.NotificationManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.top.talent.management.constants.ErrorMessages.NOTIFICATIONS_NOT_FOUND;
import static com.top.talent.management.constants.ErrorMessages.USER_NOT_FOUND_WITH_UID;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationManagementServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationManagementRepository notificationManagementRepository;

    @Mock
    private EmailCategoriesRepository emailCategoriesRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private NotificationManagementServiceImpl notificationManagementService;


    private User user;
    private Role role;
    private NotificationManagement notification;
    private NotificationFeatureDTO featureDTO;
    private EmailCategories category;


    @BeforeEach
    void setUp() {
        role = Role.builder()
                .name(RoleConstants.PRACTICE)
                .build();
        user = User.builder()
                .uuid(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .status("ACTIVE")
                .build();

        category = EmailCategories.builder()
                .categoryId(100L)
                .name("Category1")
                .description("Description1")
                .created(LocalDateTime.now())
                .createdBy("user")
                .lastUpdated(LocalDateTime.now())
                .lastUpdatedBy("user")
                .build();

        notification = NotificationManagement.builder()
                .user(user)
                .category(category)
                .notificationsEnabled(Boolean.TRUE)
                .created(LocalDateTime.now())
                .lastUpdatedBy("user")
                .lastUpdated(LocalDateTime.now())
                .createdBy("user")
                .build();

        featureDTO = NotificationFeatureDTO.builder()
                .categoryName("Category1")
                .description("Description1")
                .notificationsEnabled(Boolean.FALSE)
                .build();

    }


    @Test
    void testToggleAllNotificationsForUser_Failure_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                notificationManagementService.toggleAllNotificationsForUser(1L, false));
    }

    @Test
    void testToggleAllNotificationsForPracticeHead_Failure_NoNotificationsFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(notificationManagementRepository.findByUserUuid(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(NotificationException.class, () ->
                notificationManagementService.toggleAllNotificationsForUser(1L, false));
    }


    @Test
    void testUpdateCategoryNotificationStatusForUser_Failure_UserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .userId(1L)
                .categoryId(100L)
                .enable(true)
                .build();

        assertThrows(UserNotFoundException.class, () ->
                notificationManagementService.updateCategoryNotificationStatusForUser(requestDTO));
    }


    @Test
    void testUpdateCategoryNotificationStatusForUser_Failure_CategoryNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationManagementRepository.findByUserUuidAndCategoryCategoryId(1L, 100L))
                .thenReturn(Optional.empty());
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .userId(1L)
                .categoryId(100L)
                .enable(true)
                .build();
        assertThrows(NotificationException.class, () ->
                notificationManagementService.updateCategoryNotificationStatusForUser(requestDTO));
    }

    @Test
    void testUpdateCategoryNotificationStatusForUser_Failure_NullInputs() {
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .userId(null)
                .categoryId(null)
                .enable(true)
                .build();

        assertThrows(NotificationException.class, () ->
                notificationManagementService.updateCategoryNotificationStatusForUser(requestDTO));
    }

    @Test
    void testGetAllUsersWithNotificationpreferences_Success() {
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(role);
        when(userRepository.findAllByRole(role)).thenReturn(Collections.singletonList(user));
        when(emailCategoriesRepository.findAll()).thenReturn(Collections.singletonList(category));
        when(notificationManagementRepository.findByUserUuid(user.getUuid())).thenReturn(Collections.singletonList(notification));
        when(notificationMapper.toNotificationFeatureDTOList(Collections.singletonList(notification)))
                .thenReturn(Collections.singletonList(featureDTO));
        when(notificationMapper.toUserNotificationResponseDTO(user, Collections.singletonList(featureDTO)))
                .thenReturn(UserNotificationResponseDTO.builder()
                        .uuid(user.getUuid())
                        .email(user.getEmail())
                        .features(Collections.singletonList(featureDTO))
                        .build());

        List<UserNotificationResponseDTO> response = notificationManagementService.getAllUsersWithNotificationpreferences();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(user.getUuid(), response.get(0).getUuid());
        assertEquals(user.getEmail(), response.get(0).getEmail());
        assertEquals(1, response.get(0).getFeatures().size());
        assertEquals("Category1", response.get(0).getFeatures().get(0).getCategoryName());
    }

    @Test
    void testGetAllUsersWithNotificationpreferences_NoUsersFound() {
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(role);
        when(userRepository.findAllByRole(role)).thenReturn(Collections.emptyList());

        List<UserNotificationResponseDTO> response = notificationManagementService.getAllUsersWithNotificationpreferences();

        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    void testGetAllUsersWithNotificationpreferences_NoCategoriesFound() {
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(role);
        when(userRepository.findAllByRole(role)).thenReturn(Collections.singletonList(user));
        when(emailCategoriesRepository.findAll()).thenReturn(Collections.emptyList());
        when(notificationManagementRepository.findByUserUuid(user.getUuid())).thenReturn(Collections.emptyList());
        when(notificationMapper.toNotificationFeatureDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(notificationMapper.toUserNotificationResponseDTO(user, Collections.emptyList()))
                .thenReturn(UserNotificationResponseDTO.builder()
                        .uuid(user.getUuid())
                        .email(user.getEmail())
                        .features(Collections.emptyList())
                        .build());

        List<UserNotificationResponseDTO> response = notificationManagementService.getAllUsersWithNotificationpreferences();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(user.getUuid(), response.get(0).getUuid());
        assertEquals(user.getEmail(), response.get(0).getEmail());
        assertEquals(0, response.get(0).getFeatures().size());
    }

    @Test
    void testUpdateCategoryNotificationStatusForUser_InvalidInputs() {
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .userId(null)
                .categoryId(null)
                .enable(null)
                .build();

        assertThrows(NotificationException.class, () ->
                notificationManagementService.updateCategoryNotificationStatusForUser(requestDTO));
    }

    @Test
    void testToggleAllNotificationsForUser_ExceptionThrown() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationManagementRepository.findByUserUuid(user.getUuid())).thenReturn(Collections.emptyList());

        assertThrows(NotificationException.class, () ->
                notificationManagementService.toggleAllNotificationsForUser(1L, false));
    }

    @Test
    void testToggleAllNotificationsForUser_AuditFieldsSet() {

        try (MockedStatic<CustomUserPrincipal> mockedStatic = mockStatic(CustomUserPrincipal.class)) {
            mockedStatic.when(CustomUserPrincipal::getLoggedInUserEmail).thenReturn("audit@example.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(notificationManagementRepository.findByUserUuid(user.getUuid())).thenReturn(Collections.singletonList(notification));

            when(notificationManagementRepository.saveAll(anyList())).thenAnswer(invocation -> {
                List<NotificationManagement> notifications = invocation.getArgument(0);
                assertEquals(1, notifications.size());
                NotificationManagement updatedNotification = notifications.get(0);
                assertEquals("audit@example.com", updatedNotification.getLastUpdatedBy());

                assertNotNull(updatedNotification.getLastUpdated());
                return notifications;
            });
            notificationManagementService.toggleAllNotificationsForUser(1L, false);
        }
    }

    @Test
    void testUpdateCategoryNotificationStatusForUser_Success_AuditFields() {
        try (MockedStatic<CustomUserPrincipal> mockedStatic = mockStatic(CustomUserPrincipal.class)) {
            mockedStatic.when(CustomUserPrincipal::getLoggedInUserEmail).thenReturn("audit@example.com");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(notificationManagementRepository.findByUserUuidAndCategoryCategoryId(1L, 100L))
                    .thenReturn(Optional.of(notification));
            when(notificationManagementRepository.findByUserUuid(1L)).thenReturn(Collections.singletonList(notification));
            when(notificationMapper.toNotificationFeatureDTOList(Collections.singletonList(notification)))
                    .thenReturn(Collections.singletonList(featureDTO));
            when(notificationMapper.toUserNotificationResponseDTO(user, Collections.singletonList(featureDTO)))
                    .thenReturn(UserNotificationResponseDTO.builder()
                            .uuid(user.getUuid())
                            .email(user.getEmail())
                            .features(Collections.singletonList(featureDTO))
                            .build());

            NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                    .userId(1L)
                    .categoryId(100L)
                    .enable(false)
                    .build();
            UserNotificationResponseDTO response = notificationManagementService.updateCategoryNotificationStatusForUser(requestDTO);
            assertNotNull(response);
            assertEquals(user.getEmail(), response.getEmail());

            assertFalse(notification.isNotificationsEnabled());
            assertEquals("audit@example.com", notification.getLastUpdatedBy());
            assertNotNull(notification.getLastUpdated());
        }
    }

    @Test
    void testGetAllUsersWithNotificationPreferences_NoMissingEntries() {
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(role);
        when(userRepository.findAllByRole(role)).thenReturn(Collections.singletonList(user));
        when(emailCategoriesRepository.findAll()).thenReturn(Collections.singletonList(category));
        when(notificationManagementRepository.findByUserUuid(user.getUuid()))
                .thenReturn(Collections.singletonList(notification));
        when(notificationMapper.toNotificationFeatureDTOList(Collections.singletonList(notification)))
                .thenReturn(Collections.singletonList(featureDTO));
        when(notificationMapper.toUserNotificationResponseDTO(user, Collections.singletonList(featureDTO)))
                .thenReturn(UserNotificationResponseDTO.builder()
                        .uuid(user.getUuid())
                        .email(user.getEmail())
                        .features(Collections.singletonList(featureDTO))
                        .build());

        List<UserNotificationResponseDTO> response = notificationManagementService.getAllUsersWithNotificationpreferences();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(user.getUuid(), response.get(0).getUuid());
        assertEquals(user.getEmail(), response.get(0).getEmail());
        assertEquals(1, response.get(0).getFeatures().size());
        assertEquals("Category1", response.get(0).getFeatures().get(0).getCategoryName());
        assertEquals(Boolean.FALSE, response.get(0).getFeatures().get(0).getNotificationsEnabled());
    }

    @Test
    void testToggleAllNotificationsForUser_NoNotificationsFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationManagementRepository.findByUserUuid(1L)).thenReturn(Collections.emptyList());

        NotificationException exception = assertThrows(NotificationException.class,
                () -> notificationManagementService.toggleAllNotificationsForUser(1L, true));

        assertEquals(NOTIFICATIONS_NOT_FOUND + 1L, exception.getMessage());
    }


    @Test
    void testToggleAllNotificationsForUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> notificationManagementService.toggleAllNotificationsForUser(1L, true));

        assertEquals(USER_NOT_FOUND_WITH_UID + 1L, exception.getMessage());
    }

    @Test
    void testUpdateCategoryNotificationStatusForUser_NoNotificationsFound() {
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .userId(1L)
                .categoryId(100L)
                .enable(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationManagementRepository.findByUserUuidAndCategoryCategoryId(1L, 100L)).thenReturn(Optional.empty());

        NotificationException exception = assertThrows(NotificationException.class,
                () -> notificationManagementService.updateCategoryNotificationStatusForUser(requestDTO));

        assertEquals(NOTIFICATIONS_NOT_FOUND + 1L, exception.getMessage());
    }

    @Test
    void testToggleAllNotificationsForUser_EmptyNotifications() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationManagementRepository.findByUserUuid(1L)).thenReturn(Collections.emptyList());

        NotificationException exception = assertThrows(NotificationException.class,
                () -> notificationManagementService.toggleAllNotificationsForUser(1L, true));

        assertEquals(NOTIFICATIONS_NOT_FOUND + 1L, exception.getMessage());
        verify(notificationManagementRepository, never()).saveAll(any());
    }

    @Test
    void testToggleAllNotificationsForUser_ThrowsWhenNoNotificationsExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationManagementRepository.findByUserUuid(1L)).thenReturn(Collections.emptyList());
        NotificationException exception = assertThrows(NotificationException.class,
                () -> notificationManagementService.toggleAllNotificationsForUser(1L, false));

        assertEquals(NOTIFICATIONS_NOT_FOUND + 1L, exception.getMessage());
        verify(notificationManagementRepository, never()).saveAll(any());
    }

    @Test
    void testGetAllUsersWithNotificationpreferences_SortsFeaturesByCategoryId() {

        EmailCategories category1 = EmailCategories.builder().categoryId(100L).name("Category1").build();
        EmailCategories category2 = EmailCategories.builder().categoryId(200L).name("Category2").build();
        EmailCategories category3 = EmailCategories.builder().categoryId(50L).name("Category3").build();

        NotificationManagement notif1 = NotificationManagement.builder()
                .user(user).category(category1).notificationsEnabled(true).build();
        NotificationManagement notif2 = NotificationManagement.builder()
                .user(user).category(category2).notificationsEnabled(true).build();
        NotificationManagement notif3 = NotificationManagement.builder()
                .user(user).category(category3).notificationsEnabled(true).build();
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(role);
        when(userRepository.findAllByRole(role)).thenReturn(Collections.singletonList(user));
        when(emailCategoriesRepository.findAll()).thenReturn(List.of(category1, category2, category3));
        when(notificationManagementRepository.findByUserUuid(user.getUuid()))
                .thenReturn(List.of(notif1, notif2, notif3));

        when(notificationMapper.toNotificationFeatureDTOList(anyList()))
                .thenAnswer(inv -> {
                    List<NotificationManagement> notifications = inv.getArgument(0);
                    return notifications.stream()
                            .map(n -> NotificationFeatureDTO.builder()
                                    .categoryId(n.getCategory().getCategoryId())
                                    .categoryName(n.getCategory().getName())
                                    .notificationsEnabled(n.isNotificationsEnabled())
                                    .build())
                            .collect(toList());
                });
        when(notificationMapper.toUserNotificationResponseDTO(any(), anyList()))
                .thenAnswer(inv -> UserNotificationResponseDTO.builder()
                        .uuid(user.getUuid())
                        .email(user.getEmail())
                        .features(inv.getArgument(1))
                        .build());

        List<UserNotificationResponseDTO> result = notificationManagementService.getAllUsersWithNotificationpreferences();

        assertEquals(1, result.size());
        List<NotificationFeatureDTO> features = result.get(0).getFeatures();
        assertEquals(3, features.size());
        assertEquals(50L, features.get(0).getCategoryId());
        assertEquals(100L, features.get(1).getCategoryId());
        assertEquals(200L, features.get(2).getCategoryId());
    }

    @Test
    void testGetAllUsersWithNotificationpreferences_NoMissingEntriesWhenComplete() {
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(role);
        when(userRepository.findAllByRole(role)).thenReturn(Collections.singletonList(user));
        when(emailCategoriesRepository.findAll()).thenReturn(Collections.singletonList(category));
        when(notificationManagementRepository.findByUserUuid(user.getUuid()))
                .thenReturn(Collections.singletonList(notification));

        when(notificationMapper.toNotificationFeatureDTOList(Collections.singletonList(notification)))
                .thenReturn(Collections.singletonList(featureDTO));
        when(notificationMapper.toUserNotificationResponseDTO(user, Collections.singletonList(featureDTO)))
                .thenReturn(UserNotificationResponseDTO.builder()
                        .uuid(user.getUuid())
                        .email(user.getEmail())
                        .features(Collections.singletonList(featureDTO))
                        .build());


        List<UserNotificationResponseDTO> result = notificationManagementService.getAllUsersWithNotificationpreferences();

        verify(notificationManagementRepository, never()).saveAll(any());
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getFeatures().size());
    }

    @Test
    void testGetAllUsersWithNotificationpreferences_EmptyUsers() {
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(role);
        when(userRepository.findAllByRole(role)).thenReturn(Collections.emptyList());

        List<UserNotificationResponseDTO> result = notificationManagementService.getAllUsersWithNotificationpreferences();

        assertEquals(0, result.size());
    }

    @Test
    void testGetAllUsersWithNotificationpreferences_EmptyCategoriesAndNotifications() {
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(role);
        when(userRepository.findAllByRole(role)).thenReturn(Collections.singletonList(user));
        when(emailCategoriesRepository.findAll()).thenReturn(Collections.emptyList());
        when(notificationManagementRepository.findByUserUuid(user.getUuid()))
                .thenReturn(Collections.emptyList());
        when(notificationMapper.toUserNotificationResponseDTO(user, Collections.emptyList()))
                .thenReturn(UserNotificationResponseDTO.builder()
                        .uuid(user.getUuid())
                        .email(user.getEmail())
                        .features(Collections.emptyList())
                        .build());

        List<UserNotificationResponseDTO> result = notificationManagementService.getAllUsersWithNotificationpreferences();

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getFeatures().size());

    }


}
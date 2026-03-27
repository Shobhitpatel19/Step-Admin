package com.top.talent.management.controller;

import com.top.talent.management.dto.NotificationFeatureDTO;
import com.top.talent.management.dto.NotificationRequestDTO;
import com.top.talent.management.dto.UserNotificationResponseDTO;
import com.top.talent.management.exception.NotificationException;
import com.top.talent.management.exception.UserNotFoundException;
import com.top.talent.management.service.NotificationManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationManagementControllerTest {

    @InjectMocks
    private NotificationManagementController controller;

    @Mock
    private NotificationManagementService service;

    private UserNotificationResponseDTO sampleUserNotificationDTO;

    @BeforeEach
    void setup() {

        NotificationFeatureDTO feature = NotificationFeatureDTO.builder()
                .categoryName("Category1")
                .description("Description1")
                .notificationsEnabled(true)
                .build();

        sampleUserNotificationDTO = UserNotificationResponseDTO.builder()
                .uuid(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .features(List.of(feature))
                .build();
    }

    @Test
    void testToggleAllNotifications_UserNotFound() {
        when(service.toggleAllNotificationsForUser(anyLong(), anyBoolean()))
                .thenThrow(new UserNotFoundException("User not found"));

        Exception exception = assertThrows(UserNotFoundException.class, () ->
                controller.toggleAllPracticeHeadNotifications(1L, true)
        );

        assertEquals("User not found", exception.getMessage());
        verify(service, times(1)).toggleAllNotificationsForUser(1L, true);
    }

    @Test
    void testGetUsersWithFeatures_EmptyResponse() {
        when(service.getAllUsersWithNotificationpreferences()).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserNotificationResponseDTO>> response = controller.getPracticeHeadsWithFeatures();

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(service, times(1)).getAllUsersWithNotificationpreferences();
    }

    @Test
    void testGetUsersWithFeatures_Success() {
        when(service.getAllUsersWithNotificationpreferences()).thenReturn(List.of(sampleUserNotificationDTO));

        ResponseEntity<List<UserNotificationResponseDTO>> response = controller.getPracticeHeadsWithFeatures();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstName());
        assertEquals("Category1", response.getBody().get(0).getFeatures().get(0).getCategoryName());
        assertTrue(response.getBody().get(0).getFeatures().get(0).getNotificationsEnabled());

        verify(service, times(1)).getAllUsersWithNotificationpreferences();
    }

    @Test
    void testToggleAllNotifications_Success() {
        when(service.toggleAllNotificationsForUser(1L, true)).thenReturn(sampleUserNotificationDTO);

        ResponseEntity<UserNotificationResponseDTO> response = controller.toggleAllPracticeHeadNotifications(1L, true);

        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Category1", response.getBody().getFeatures().get(0).getCategoryName());
        verify(service, times(1)).toggleAllNotificationsForUser(1L, true);
    }

    @Test
    void testToggleNotification_Success() {
        // Create NotificationRequestDTO with @Builder
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .userId(1L)
                .categoryId(100L)
                .enable(true)
                .build();

        when(service.updateCategoryNotificationStatusForUser(requestDTO)).thenReturn(sampleUserNotificationDTO);

        ResponseEntity<UserNotificationResponseDTO> response = controller.toggleNotification(requestDTO);

        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Category1", response.getBody().getFeatures().get(0).getCategoryName());
        assertTrue(response.getBody().getFeatures().get(0).getNotificationsEnabled());
        verify(service, times(1)).updateCategoryNotificationStatusForUser(requestDTO);
    }

    @Test
    void testToggleNotification_UserNotFound() {
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .userId(999L)
                .categoryId(100L)
                .enable(true)
                .build();

        when(service.updateCategoryNotificationStatusForUser(requestDTO))
                .thenThrow(new UserNotFoundException("User not found"));

        Exception exception = assertThrows(UserNotFoundException.class, () ->
                controller.toggleNotification(requestDTO)
        );

        assertEquals("User not found", exception.getMessage());
        verify(service, times(1)).updateCategoryNotificationStatusForUser(requestDTO);
    }

    @Test
    void testToggleNotification_CategoryNotFound() {
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .userId(1L)
                .categoryId(999L)
                .enable(true)
                .build();

        when(service.updateCategoryNotificationStatusForUser(requestDTO))
                .thenThrow(new NotificationException("Notification not found"));

        Exception exception = assertThrows(NotificationException.class, () ->
                controller.toggleNotification(requestDTO)
        );

        assertEquals("Notification not found", exception.getMessage());
        verify(service, times(1)).updateCategoryNotificationStatusForUser(requestDTO);
    }

    @Test
    void testGetPracticeHeadsWithFeatures_Success() {
        when(service.getAllUsersWithNotificationpreferences())
                .thenReturn(List.of(sampleUserNotificationDTO));

        ResponseEntity<List<UserNotificationResponseDTO>> response = controller.getPracticeHeadsWithFeatures();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(sampleUserNotificationDTO, response.getBody().get(0));

        verify(service, times(1)).getAllUsersWithNotificationpreferences();
    }

    @Test
    void testToggleNotification_InvalidRequest() {
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .userId(null)
                .categoryId(null)
                .enable(true)
                .build();

        when(service.updateCategoryNotificationStatusForUser(requestDTO))
                .thenThrow(new IllegalArgumentException("Invalid notification request"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                controller.toggleNotification(requestDTO)
        );

        assertEquals("Invalid notification request", exception.getMessage());
        verify(service, times(1)).updateCategoryNotificationStatusForUser(requestDTO);
    }
}
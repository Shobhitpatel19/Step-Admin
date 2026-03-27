package com.top.talent.management.mapper;

import com.top.talent.management.dto.NotificationFeatureDTO;
import com.top.talent.management.dto.UserNotificationResponseDTO;
import com.top.talent.management.entity.NotificationManagement;
import com.top.talent.management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {


    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.description", target = "description")
    @Mapping(source = "notificationsEnabled", target = "notificationsEnabled")
    @Mapping(source = "category.categoryId", target = "categoryId")
    NotificationFeatureDTO toNotificationFeatureDTO(NotificationManagement notificationManagement);


    List<NotificationFeatureDTO> toNotificationFeatureDTOList(List<NotificationManagement> notifications);


    @Mapping(source = "user.uuid", target = "uuid")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.practice", target = "practice")
    @Mapping(source = "features", target = "features")
    UserNotificationResponseDTO toUserNotificationResponseDTO(User user, List<NotificationFeatureDTO> features);
}

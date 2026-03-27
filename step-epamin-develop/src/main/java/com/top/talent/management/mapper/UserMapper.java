package com.top.talent.management.mapper;

import com.top.talent.management.dto.UserResponseDTO;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.User;
import com.top.talent.management.constants.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target ="isDelegate", source = "delegate")
    @Mapping(target = "isActive", source = "status")
    UserDTO toUserDTO(User user);

    @Mapping(target = "roleName", source = "user.role.name")
    @Mapping(target = "message", expression = "java(msg)")
    UserResponseDTO toUserResponseDTO(User user, String msg);

    @Mapping(target = "uuid", source = "requestDTO.uuid")
    @Mapping(target = "email", source = "requestDTO.email")
    @Mapping(target = "firstName", source = "requestDTO.firstName")
    @Mapping(target = "lastName", source = "requestDTO.lastName")
    @Mapping(target = "practice", source = "requestDTO.practice")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", constant = Constants.USER_STATUS_ACTIVE)
    @Mapping(target = "lastUpdated", expression = "java(now)")
    @Mapping(target = "lastUpdatedBy", expression = "java(currentUsername)")
    User userDTOToUser(UserDTO requestDTO, Role role, String currentUsername, LocalDateTime now);


    @Mapping(target = "uuid", source = "uid")
    @Mapping(target = "practice", source = "competencyPractice")
    User toUser(TopTalentEmployee topTalentEmployee);

}

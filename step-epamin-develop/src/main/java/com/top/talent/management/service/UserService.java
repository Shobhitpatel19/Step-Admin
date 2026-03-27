package com.top.talent.management.service;

import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserResponseDTO;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    UserDTO getUser(Authentication authentication);

    UserDTO getUser(String email);

    UserDTO registerUser(User user);

    UserDTO removeUser(String email);

    List<UserDTO> getAllUsers();

    List<UserDTO> getUsersByPractice(String practice, Authentication authentication);

    List<UserResponseDTO> getUsersByRole(String roleName, Optional<String> status);

    UserResponseDTO addUser(UserDTO requestDTO, String currentUsername);

    UserResponseDTO deactivateUser(Long uid, String currentUsername);

    List<User> addEligibleUsers(List<TopTalentEmployee> users);

    List<UserDTO> getUsersWithPracticeHeadRole();

    UserDTO getPracticeHeadByCompetency(String competency);
    Role getUserRoleFromEmail(String email);

    Long getUuidByEmail(String email);

    String getUserStatusByEmail(String email);
}

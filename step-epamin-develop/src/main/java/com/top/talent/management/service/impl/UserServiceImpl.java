package com.top.talent.management.service.impl;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserResponseDTO;
import com.top.talent.management.entity.EmailCategories;
import com.top.talent.management.entity.NotificationManagement;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.PracticeDelegationException;
import com.top.talent.management.exception.TopTalentEmployeeException;
import com.top.talent.management.exception.UserNotFoundException;
import com.top.talent.management.helper.SuperAdminServiceHelper;
import com.top.talent.management.mapper.UserMapper;
import com.top.talent.management.repository.EmailCategoriesRepository;
import com.top.talent.management.repository.NotificationManagementRepository;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.top.talent.management.constants.Constants.USER_STATUS_ACTIVE;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final SuperAdminServiceHelper superAdminServiceHelper;

    private final UserMapper userMapper;

    private final NotificationManagementRepository notificationManagementRepository;

    private final EmailCategoriesRepository emailCategoriesRepository;



    @Override
    public UserDTO getUser(Authentication authentication) {
        String email = validateAuthenticationAndGetEmail(authentication);
        log.info("Getting user with email {}", email);
        return userMapper.toUserDTO(userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND_WITH_EMAIL + email)));
    }

    @Override
    public UserDTO getUser(String email) {
        return userRepository.findByEmail(email).map(userMapper::toUserDTO).orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND_WITH_EMAIL + email));
    }

    @Override
    public UserDTO registerUser(User user) {
        User savedUser = userRepository.save(user);
        setupNotificationManagementForNewUser(savedUser);

        return userMapper.toUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO removeUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        optionalUser.ifPresent(userRepository::delete);
        optionalUser.ifPresent(user -> {
            List<NotificationManagement> notifications = user.getNotifications();
            if (notifications != null) {
                notificationManagementRepository.deleteAll(notifications);
            }
        });
        return optionalUser.map(userMapper::toUserDTO).orElse(null);
    }

    @Override
    public Role getUserRoleFromEmail(String email) {
        log.info("Getting user roles with email {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND_WITH_EMAIL)).getRole();
    }


    @Override
    public List<User> addEligibleUsers(List<TopTalentEmployee> users) {
        List<User> savedUsers;
        try {
            savedUsers = userRepository.saveAll(users.stream().map((superAdminServiceHelper::generateUserObject)).toList());
        } catch (Exception e) {
            log.error("Error while saving users: {}", e.getMessage());
            throw new TopTalentEmployeeException(ErrorMessages.USERS_NOT_SAVED);
        }
        return savedUsers;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll().stream().collect(ArrayList::new, (list, user) -> list.add(userMapper.toUserDTO(user)), ArrayList::addAll);
    }

    @Override
    public List<UserDTO> getUsersByPractice(String practice, Authentication authentication) {
        String email = validateAuthenticationAndGetEmail(authentication);
        log.info("Getting all users by practice {} with email {}", practice, email);

        if (StringUtils.isEmpty(practice)) {
            throw new IllegalArgumentException(ErrorMessages.PRACTICE_NOT_NULL);
        }
        String practiceName = userRepository.findByEmail(email).get().getPractice();
        log.info("Practice name of email {} is {}", email, practiceName);
        if (!practiceName.equals(practice)) {
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
        }
        return userRepository.findByPractice(practice).stream().collect(ArrayList::new, (list, user) -> list.add(userMapper.toUserDTO(user)), ArrayList::addAll);
    }

    private String validateAuthenticationAndGetEmail(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
        }
        String email;
        if (authentication.getPrincipal() instanceof CustomUserPrincipal customUserPrincipal) {
            email = customUserPrincipal.getEmail();
        } else if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            email = oidcUser.getEmail();
        } else {
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
        }

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ErrorMessages.USER_NOT_FOUND_WITH_EMAIL + email);
        }
        if (userRepository.findByEmail(email).isEmpty()) {
                throw new UserNotFoundException(ErrorMessages.USER_NOT_FOUND_WITH_UID + email);
        }
        if (!Boolean.TRUE.equals(isUserActive(user.get()))) {
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
        }
        return email;
    }

    public Boolean isUserActive (User user){
        return USER_STATUS_ACTIVE.equals(user.getStatus());
    }

    @Override
    public List<UserResponseDTO> getUsersByRole (String roleName, Optional < String > status){
        log.info("Getting all users by role: {}", roleName);
        status.ifPresent(s -> log.info("User status is: {}", s));

        Role role = roleRepository.findByName(roleName);

        return userRepository.findAllByRole(role).stream()
                .filter(user -> status.isEmpty() || user.getStatus().equals(status.get()))
                .map(user -> userMapper.toUserResponseDTO(user, null))
                .toList();
    }

    @Override
    public UserResponseDTO addUser (UserDTO requestDTO, String currentUsername){
        LocalDateTime now = LocalDateTime.now();
        Role role = roleRepository.findByName(requestDTO.getRoleName());
        Optional<User> userOptional = userRepository.findById(requestDTO.getUuid());
        User user = userMapper.userDTOToUser(requestDTO, role, currentUsername, now);
        String msg;

        if (userOptional.isPresent()) {
            log.info("Updating user role from {} to {}", userOptional.get().getRole().getName(), requestDTO.getRoleName());

            msg = Constants.USER_DETAILS_UPDATED;
        } else {
            log.info("Adding new user with role {}", requestDTO.getRoleName());

            setCreationDetails(user, currentUsername, now);
            msg = Constants.USER_ADDED;
        }
        setupNotificationManagementForNewUser(user);

        userRepository.save(user);



        return userMapper.toUserResponseDTO(user, msg);
    }

    private void setCreationDetails (User user, String currentUser, LocalDateTime now){
        user.setCreated(now);
        user.setCreatedBy(currentUser);
    }

    @Override
    public UserResponseDTO deactivateUser (Long uid, String currentUsername){
        log.info("Deactivating user with uid {}", uid);
        User user = userRepository.findById(uid).orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND_WITH_UID + uid));
        if (user.getNotifications() != null) {
            user.getNotifications().clear();
        }
        deactivateSetUpdateDetails(user, currentUsername);

        userRepository.save(user);

        return userMapper.toUserResponseDTO(user, Constants.USER_INACTIVE);
    }

    private void deactivateSetUpdateDetails (User user, String currentUser){
        user.setStatus(Constants.USER_STATUS_INACTIVE);
        user.setLastUpdated(LocalDateTime.now());
        user.setLastUpdatedBy(currentUser);
    }

    @Override
    public List<UserDTO> getUsersWithPracticeHeadRole() {
        Role practiceRole=roleRepository.findByName(RoleConstants.PRACTICE);
        List<User> users=userRepository.findAllByRole(practiceRole);
        return users.stream()
                .filter(user->!user.isDelegate())
                .map(user -> UserDTO.builder()
                        .uuid(user.getUuid())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .practice(user.getPractice())
                        .roleName(user.getRole().getName())
                        .isActive(user.getStatus())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getPracticeHeadByCompetency(String competency) {
        Optional<User> userOptional = userRepository
                .findByPracticeAndRoleAndIsDelegate(competency, roleRepository.findByName(RoleConstants.PRACTICE),Boolean.FALSE);
        if (userOptional.isEmpty()){
            throw new PracticeDelegationException(ErrorMessages.PRACTICE_HEAD_NOT_FOUND);
        }
        return userMapper.toUserDTO(userOptional.orElse(null));
    }


    @Override
    public String getUserStatusByEmail(String email){
        return userRepository.
                findByEmail(email).orElseThrow(()->new RuntimeException("User not found with email "+ email))
                .getStatus();
    }


    @Override
    public Long getUuidByEmail(String email) {
        return userRepository.
                findByEmail(email).orElseThrow(()->new RuntimeException("User not found with email "+ email))
                .getUuid();
    }
    private void setupNotificationManagementForNewUser(User user) {
        if (user.getRole() == null || !RoleConstants.PRACTICE.equals(user.getRole().getName())) {
            log.debug("User is not a Practice Head. Skipping Notification Management setup.");
            return;
        }

        log.info("Setting up Notification Management entries for Practice Head: {}", user.getEmail());

        List<EmailCategories> emailCategories = emailCategoriesRepository.findAll();

        for (EmailCategories emailCategory : emailCategories) {
            Boolean exists = notificationManagementRepository.existsByUserAndCategory(user, emailCategory);

            if (!exists) {
                NotificationManagement notificationManagement = NotificationManagement.builder()
                        .user(user)
                        .category(emailCategory)
                        .notificationsEnabled(true)
                        .created(LocalDateTime.now())
                        .createdBy(CustomUserPrincipal.getLoggedInUserEmail())
                        .lastUpdated(LocalDateTime.now())
                        .lastUpdatedBy(CustomUserPrincipal.getLoggedInUserEmail())
                        .build();

                notificationManagementRepository.save(notificationManagement);
                log.info("Created NotificationManagement for user {} and category {}.",
                        user.getEmail(), emailCategory.getName());
            }
        }

        log.info("Notification Management setup completed for user: {}", user.getEmail());
    }
}


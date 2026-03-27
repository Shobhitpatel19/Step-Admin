package com.top.talent.management.service.impl;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.PracticeDelegationDTO;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.entity.Delegation;
import com.top.talent.management.entity.PracticeDelegationFeature;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.PracticeDelegationException;
import com.top.talent.management.exception.UserNotFoundException;
import com.top.talent.management.mapper.PracticeDelegationMapper;
import com.top.talent.management.repository.PracticeDelegationFeatureRepository;
import com.top.talent.management.repository.PracticeDelegationRepository;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.DelegationService;
import com.top.talent.management.service.PracticeDelegateEmailService;
import com.top.talent.management.service.UserProfileService;
import com.top.talent.management.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.top.talent.management.constants.Constants.REQUIRED_JOB_LEVELS;
import static com.top.talent.management.constants.Constants.USER_STATUS_ACTIVE;

@Slf4j
@RequiredArgsConstructor
@Service
public class DelegationServiceImpl implements DelegationService {
    private final PracticeDelegationRepository practiceDelegationRepository;
    private final PracticeDelegationFeatureRepository practiceDelegationFeatureRepository;
    private final UserProfileService userProfileService;
    private final PracticeDelegationMapper practiceDelegationMapper;
    private final UserService userService;
    private final PracticeDelegateEmailService practiceDelegateEmailService;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public PracticeDelegationDTO createPracticeDelegate(Delegation delegation, String competency) {
        String loggedInUserEmail = CustomUserPrincipal.getLoggedInUserEmail();
        UserDTO loggedInUser = userService.getUser(loggedInUserEmail);

        if(RoleConstants.PRACTICE.equals(loggedInUser.getRoleName()) && !competency.isBlank()){
            throw new PracticeDelegationException(ErrorMessages.NOT_SEND_COMPETENCY_PH);
        }
        else if(RoleConstants.SUPER_ADMIN.equals(loggedInUser.getRoleName()) && competency.isBlank()){
            throw new PracticeDelegationException(ErrorMessages.COMPETENCY_NOT_SENT);
        }
        UserDTO practiceHead = getPracticeHead(competency);
        Delegation existingDelegate = getExistingDelegate(competency, loggedInUserEmail, practiceHead);

        UserProfile epamEmployeeProfile = userProfileService.fetchUserByEmail(delegation.getDelegatedTo());

        checkForValidations(existingDelegate, delegation, epamEmployeeProfile, competency);
        List<PracticeDelegationFeature> practiceDelegationFeatures = validateAndFetchFeatures(delegation);
        setDelegatedBy(delegation, loggedInUserEmail, practiceHead);
        delegation.setPracticeDelegationFeatures(practiceDelegationFeatures);

        handleUserRegistration(existingDelegate, delegation, epamEmployeeProfile, competency);

        PracticeDelegationDTO practiceDelegationDTO = saveAndMapDelegation(delegation, epamEmployeeProfile);

        sendNotificationIfNewDelegate(delegation, existingDelegate, epamEmployeeProfile);

        return practiceDelegationDTO;
    }

    private UserDTO getPracticeHead(String competency) {
        return competency.isBlank() ? userService.getUser(CustomUserPrincipal.getLoggedInUserEmail()) : userService.getPracticeHeadByCompetency(competency);
    }

    private Delegation getExistingDelegate(String competency, String loggedInUserEmail, UserDTO practiceHead) {
        return competency.isBlank()
                ? practiceDelegationRepository.findByDelegatedBy(loggedInUserEmail)
                : practiceDelegationRepository.findByDelegatedBy(practiceHead.getEmail());
    }

    private List<PracticeDelegationFeature> validateAndFetchFeatures(Delegation delegation) {
        List<String> featureNames = delegation.getPracticeDelegationFeatures().stream()
                .map(practiceDelegationFeature -> practiceDelegationFeature.getName().trim())
                .toList();
        log.info("Looking for features: {}", featureNames);

        List<PracticeDelegationFeature> practiceDelegationFeatures =
                practiceDelegationFeatureRepository.findByNameIn(featureNames);

        if (practiceDelegationFeatures.size() != featureNames.size()) {
            throw new PracticeDelegationException(ErrorMessages.PRACTICE_DELEGATION_INVALID_FEATURE_SELECTED);
        }
        return practiceDelegationFeatures;
    }

    private void setDelegatedBy(Delegation delegation, String loggedInUserEmail, UserDTO practiceHead) {
        delegation.setDelegatedBy(practiceHead == null ? loggedInUserEmail : practiceHead.getEmail());
    }

    private void handleUserRegistration(Delegation existingDelegate, Delegation delegation, UserProfile epamEmployeeProfile, String competency) {
        if (existingDelegate == null) {
            delegation.setCreatedByAndUpdatedBy(CustomUserPrincipal.getLoggedInUser());
            userService.registerUser(createDelegateUser(epamEmployeeProfile, competency));
        } else {
            delegation.setUpdatedBy(CustomUserPrincipal.getLoggedInUser());
        }
    }

    private PracticeDelegationDTO saveAndMapDelegation(Delegation delegation, UserProfile epamEmployeeProfile) {
        PracticeDelegationDTO practiceDelegationDTO = practiceDelegationMapper.toPracticeDelegationDto(

                practiceDelegationRepository.save(delegation));
        log.info("Getting called");
        practiceDelegationDTO.setDelegatedTo(epamEmployeeProfile);
        return practiceDelegationDTO;
    }

    private void sendNotificationIfNewDelegate(Delegation newDelegate, Delegation existingDelegate, UserProfile epamEmployeeProfile) {
        if (existingDelegate == null) {
            practiceDelegateEmailService.sendNotificationMailToDelegate(
                    newDelegate, epamEmployeeProfile.getFullName(), epamEmployeeProfile.getEmail(), "create");
        } else {
            practiceDelegateEmailService.sendNotificationMailToDelegate(newDelegate, epamEmployeeProfile.getFullName(), epamEmployeeProfile.getEmail(), "update");
        }
    }


    private User createDelegateUser(UserProfile apiSearchEmployeeDTO, String competency) {
        User user = User.builder()
                .uuid(apiSearchEmployeeDTO.getUid())
                .firstName(apiSearchEmployeeDTO.getFirstName())
                .lastName(apiSearchEmployeeDTO.getLastName())
                .email(apiSearchEmployeeDTO.getEmail())
                .status(USER_STATUS_ACTIVE)
                .role(roleRepository.findByName(RoleConstants.PRACTICE))
                .isDelegate(true)
                .build();

        if (competency.isBlank()) {
            UserDTO practiceHead = userService.getUser(CustomUserPrincipal.getLoggedInUserEmail());
            user.setPractice(practiceHead.getPractice());
        } else {
            user.setPractice(competency);
        }

        user.setCreatedByAndUpdatedBy(CustomUserPrincipal.getLoggedInUser());
        return user;
    }

    private void checkForValidations(Delegation existingDelegate, Delegation delegation, UserProfile selectedUser, String competency) {
        validateExistingDelegate(existingDelegate, delegation);
        validateUserNotStepUser(delegation, competency);
        validateSelectedUser(selectedUser);
        validateUserEligibility(selectedUser);
        validateDelegationFeatures(delegation);
        validateApprovalRequired(delegation);
    }

    private void validateExistingDelegate(Delegation existingDelegate, Delegation delegation) {
        if (existingDelegate != null) {
            if (!existingDelegate.getDelegatedTo().equals(delegation.getDelegatedTo())) {
                throw new PracticeDelegationException(ErrorMessages.PRACTICE_DELEGATION_ALREADY_DELEGATED);
            }
            delegation.setId(existingDelegate.getId());
        }
    }

    private void validateUserNotStepUser(Delegation delegation, String competency) {
        UserDTO userDTO;
        try {
            userDTO = userService.getUser(delegation.getDelegatedTo());
            String existingDelegateCompetency = userDTO.getPractice();
            log.info("{} {}", existingDelegateCompetency, getPracticeHead(competency).getPractice());
            if (!userDTO.isDelegate() || (competency.isBlank() && !existingDelegateCompetency.equals(getPracticeHead(competency).getPractice()))
                    || (!competency.isBlank() && !existingDelegateCompetency.equals(competency))) {
                throw new PracticeDelegationException(ErrorMessages.PRACTICE_DELEGATION_IS_STEP_USER);
            }
        }
        catch(UserNotFoundException ignored){
            log.info("User {} not found", delegation.getDelegatedTo());
            log.error(ignored.getMessage());
        }



    }

    private void validateSelectedUser(UserProfile selectedUser) {
        if (selectedUser == null) {
            throw new PracticeDelegationException(ErrorMessages.PRACTICE_DELEGATION_USER_NOT_FOUND);
        }
    }

    private void validateUserEligibility(UserProfile selectedUser) {
        if (!REQUIRED_JOB_LEVELS.contains(selectedUser.getJobTrack() + selectedUser.getJobTrackLevel())) {
            throw new PracticeDelegationException(ErrorMessages.PRACTICE_DELEGATION_USER_NOT_ELIGIBLE);
        }
    }

    private void validateDelegationFeatures(Delegation delegation) {
        if (delegation.getPracticeDelegationFeatures() == null || delegation.getPracticeDelegationFeatures().isEmpty()) {
            throw new PracticeDelegationException(ErrorMessages.PRACTICE_DELEGATION_NO_FEATURE_SELECTED);
        }
    }

    private void validateApprovalRequired(Delegation delegation) {
        if (delegation.getApprovalRequired() == null) {
            throw new PracticeDelegationException(ErrorMessages.PRACTICE_DELEGATION_NO_ACCESS_LEVEL_SELECTED);
        }
    }

    @Override
    public PracticeDelegationDTO getPracticeDelegate(String competency) {
        String loggedInUserEmail = CustomUserPrincipal.getLoggedInUserEmail();
        UserDTO loggedInUser = userService.getUser(loggedInUserEmail);
        if(RoleConstants.PRACTICE.equals(loggedInUser.getRoleName()) && !competency.isBlank()){
            throw new PracticeDelegationException(ErrorMessages.NOT_SEND_COMPETENCY_PH);
        }
        else if(RoleConstants.SUPER_ADMIN.equals(loggedInUser.getRoleName()) && competency.isBlank()){
            throw new PracticeDelegationException(ErrorMessages.COMPETENCY_NOT_SENT);
        }
        Delegation delegation = getDelegationByPracticeHead(competency);
        PracticeDelegationDTO practiceDelegationDTO = practiceDelegationMapper.toPracticeDelegationDto(delegation);
        practiceDelegationDTO.setDelegatedTo(userProfileService.fetchUserByEmail(delegation.getDelegatedTo()));
        return practiceDelegationDTO;
    }

    @Override
    public PracticeDelegationDTO getPracticeDelegateByDelegatedTo() {
        String loggedInUserEmail = CustomUserPrincipal.getLoggedInUserEmail();
        Delegation delegation = practiceDelegationRepository.findByDelegatedTo(loggedInUserEmail);
        if (delegation == null) {
            throw new PracticeDelegationException(ErrorMessages.PRACTICE_DELEGATION_DELEGATE_NOT_FOUND);
        }

        PracticeDelegationDTO practiceDelegationDTO = practiceDelegationMapper.toPracticeDelegationDto(delegation);
        practiceDelegationDTO.setDelegatedTo(userProfileService.fetchUserByEmail(delegation.getDelegatedTo()));
        return practiceDelegationDTO;
    }

    @Override
    @Transactional
    public PracticeDelegationDTO deletePracticeDelegate(String competency) {
        Delegation delegation = getDelegationByPracticeHead(competency);
        PracticeDelegationDTO practiceDelegationDTO = practiceDelegationMapper.toPracticeDelegationDto(delegation);
        practiceDelegationDTO.setDelegatedTo(userProfileService.fetchUserByEmail(delegation.getDelegatedTo()));

        delegation.getPracticeDelegationFeatures().clear();
        UserDTO removedUser = userService.removeUser(delegation.getDelegatedTo());
        practiceDelegationRepository.save(delegation);
        practiceDelegationRepository.delete(delegation);
        practiceDelegateEmailService.sendNotificationMailToDelegate(delegation, removedUser.getFirstName() + " " + removedUser.getLastName(), delegation.getDelegatedTo(), "delete");

        return practiceDelegationDTO;
    }

    private Delegation getDelegationByPracticeHead(String competency) {
        String practiceHeadEmail = competency.isBlank()
                ? CustomUserPrincipal.getLoggedInUserEmail()
                : userService.getPracticeHeadByCompetency(competency).getEmail();

        Delegation delegation = practiceDelegationRepository.findByDelegatedBy(practiceHeadEmail);
        if (delegation == null) {
            throw new PracticeDelegationException(ErrorMessages.PRACTICE_DELEGATION_NEVER_DELEGATED);
        }
        return delegation;
    }


}

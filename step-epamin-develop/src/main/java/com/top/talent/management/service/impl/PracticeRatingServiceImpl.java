package com.top.talent.management.service.impl;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.CategoryDTO;
import com.top.talent.management.dto.EmployeeRatingRequestDTO;
import com.top.talent.management.dto.EmployeeRatingResponseDTO;
import com.top.talent.management.dto.PracticeEmployeeDTO;
import com.top.talent.management.dto.PracticeRatingResponseDTO;
import com.top.talent.management.dto.SubCategoryDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.entity.Category;
import com.top.talent.management.entity.EmployeeRating;
import com.top.talent.management.entity.SubCategory;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.PracticeDelegationException;
import com.top.talent.management.exception.PracticeRatingException;
import com.top.talent.management.exception.UserNotFoundException;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.repository.PracticeRatingRepository;
import com.top.talent.management.repository.SubCategoryRepository;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.MeanRatingService;
import com.top.talent.management.service.PracticeDelegateUserService;
import com.top.talent.management.service.PracticeRatingService;
import com.top.talent.management.service.TopTalentExcelVersionService;
import com.top.talent.management.service.UserProfileService;
import com.top.talent.management.service.ValidateCandidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PracticeRatingServiceImpl implements PracticeRatingService {

    private final ValidateCandidateService validateCandidateService;

    private final TopTalentExcelVersionService topTalentExcelVersionService;

    private final MeanRatingService meanRatingService;

    private final UserProfileService userProfileService;

    private final PracticeRatingRepository practiceRatingRepository;

    private final SubCategoryRepository subCategoryRepository;

    private final TopTalentEmployeeRepository topTalentEmployeeRepository;

    private final UserRepository userRepository;

    private final TopTalentEmployeeMapper topTalentEmployeeMapper;

    private final PracticeDelegateUserService practiceDelegateUserService;

    @Override
    public EmployeeRatingResponseDTO getEmployeeRating(Long uid, CustomUserPrincipal customUserPrincipal) {
        TopTalentExcelVersion topTalentExcelVersion = topTalentExcelVersionService.findLatestVersion();
        TopTalentEmployee topTalentEmployee = validateCandidateService.isValidCandidate(uid, customUserPrincipal, topTalentExcelVersion);

        EmployeeRatingResponseDTO employeeRatingResponseDTO = createInitialEmployeeRatingResponseDTO(topTalentEmployee, topTalentExcelVersion, uid);

        List<EmployeeRating> employeeRatings = practiceRatingRepository.findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion);

        if (employeeRatings.isEmpty()) {
            processDefaultRatings(employeeRatingResponseDTO, uid, topTalentExcelVersion);
        } else {
            processExistingRatings(employeeRatingResponseDTO, employeeRatings, uid, topTalentExcelVersion);
        }
        return employeeRatingResponseDTO;
    }

    private EmployeeRatingResponseDTO createInitialEmployeeRatingResponseDTO(TopTalentEmployee topTalentEmployee, TopTalentExcelVersion topTalentExcelVersion, Long uid) {
        return EmployeeRatingResponseDTO.builder()
                .jobTitle(topTalentEmployee.getTitle())
                .primarySkill(topTalentEmployee.getPrimarySkill())
                .name(topTalentEmployee.getName())
                .status(meanRatingService.getSubmissionStatus(uid, topTalentExcelVersion))
                .mean(meanRatingService.getMeanRating(uid, topTalentExcelVersion))
                .build();
    }

    private void processDefaultRatings(EmployeeRatingResponseDTO employeeRatingResponseDTO, Long uid, TopTalentExcelVersion topTalentExcelVersion) {
        log.info("Getting default ratings for uid {} for the year {} with version {}", uid, topTalentExcelVersion.getUploadedYear(), topTalentExcelVersion.getVersionName());

        getDefaultEmployeeResponseDTO(employeeRatingResponseDTO);
    }

    private void processExistingRatings(EmployeeRatingResponseDTO employeeRatingResponseDTO, List<EmployeeRating> employeeRatings, Long uid, TopTalentExcelVersion topTalentExcelVersion) {
        log.info("Getting existing ratings for uid {} for the year {} with version {}", uid, topTalentExcelVersion.getUploadedYear(), topTalentExcelVersion.getVersionName());

        convertToDTO(employeeRatingResponseDTO, employeeRatings);
        SubmissionStatus status = employeeRatingResponseDTO.getStatus();
        String msg;
        if(status == SubmissionStatus.A) {
            msg = Constants.APPROVED_STATUS;
        }
        else {
            msg = (status == SubmissionStatus.S)? Constants.SUBMIT_STATUS : Constants.DRAFT_STATUS;
        }
        employeeRatingResponseDTO.setMessage(msg);
    }

    private void convertToDTO(EmployeeRatingResponseDTO employeeRatingResponseDTO, List<EmployeeRating> employeeRatings) {
        Map<Category, List<SubCategoryDTO>> categoryMap = employeeRatings.stream()
                .collect(Collectors.groupingBy(
                        rating -> rating.getSubCategory().getCategory(),
                        Collectors.mapping(this::createSubCategoryDTOWithRating,
                                Collectors.toList())
                ));

        List<CategoryDTO> categoryDTOs = createCategoryDTOList(categoryMap);
        employeeRatingResponseDTO.setCategories(categoryDTOs);
    }

    private SubCategoryDTO createSubCategoryDTOWithRating(EmployeeRating rating) {
        return SubCategoryDTO.builder()
                .subCategoryName(rating.getSubCategory().getSubCategoryName())
                .description(rating.getSubCategory().getDescription())
                .employeeRating(rating.getRating())
                .build();
    }

    private void getDefaultEmployeeResponseDTO(EmployeeRatingResponseDTO employeeRatingResponseDTO) {
        List<SubCategory> subCategories = subCategoryRepository.findAll();

        Map<Category, List<SubCategoryDTO>> categoryMap = subCategories.stream()
                .collect(Collectors.groupingBy(
                        SubCategory::getCategory,
                        Collectors.mapping(this::createSubCategoryDTOWithDefaultRating,
                                Collectors.toList())
                ));

        List<CategoryDTO> categoryDTOs = createCategoryDTOList(categoryMap);
        employeeRatingResponseDTO.setCategories(categoryDTOs);
    }

    private SubCategoryDTO createSubCategoryDTOWithDefaultRating(SubCategory subCategory) {
        return SubCategoryDTO.builder()
                .subCategoryName(subCategory.getSubCategoryName())
                .description(subCategory.getDescription())
                .employeeRating(null)
                .build();
    }

    private List<CategoryDTO> createCategoryDTOList(Map<Category, List<SubCategoryDTO>> categoryMap) {
        return categoryMap.entrySet().stream()
                .map(entry -> CategoryDTO.builder()
                        .categoryName(entry.getKey().getCategoryName())
                        .subCategory(entry.getValue())
                        .build())
                .toList();
    }

    @Transactional
    @Override
    public EmployeeRatingResponseDTO saveEmployeeRatings(Long uid, SubmissionStatus submissionStatus, EmployeeRatingRequestDTO requestDTO, CustomUserPrincipal customUserPrincipal) {

        String principalFullName = customUserPrincipal.getFullName();
        LocalDateTime now = LocalDateTime.now();
        TopTalentExcelVersion topTalentExcelVersion = topTalentExcelVersionService.findLatestVersion();
        User loggedInUser = userRepository.findByEmail(customUserPrincipal.getEmail())
                .orElseThrow(() -> new PracticeRatingException(ErrorMessages.USER_NOT_FOUND_WITH_EMAIL + customUserPrincipal.getEmail()));
        if (loggedInUser.getUuid().equals(uid) && validateCandidateService.isValidCandidate(uid, customUserPrincipal, topTalentExcelVersion) != null) {
            throw new PracticeRatingException(ErrorMessages.SELF_RATING_NOT_ALLOWED);
        }
        if (customUserPrincipal.isDelegate() && submissionStatus == SubmissionStatus.A && Boolean.TRUE == practiceDelegateUserService.isApprovalRequired()) {
            throw new PracticeDelegationException(ErrorMessages.PRACTICE_RATING_NO_APPROVAL_PERMISSION);
        }

        log.info("Saving employee ratings for uid {} for the year {} with version {}", uid, topTalentExcelVersion.getUploadedYear(), topTalentExcelVersion.getVersionName());

        requestDTO.getCategories().stream()
                .flatMap(category -> category.getSubCategory().stream())
                .forEach(subCategoryDTO -> {
                    SubCategory subCategory = subCategoryRepository.findBySubCategoryName(subCategoryDTO.getSubCategoryName());
                    EmployeeRating employeeRating = EmployeeRating.builder()
                            .uid(uid)
                            .rating(subCategoryDTO.getEmployeeRating())
                            .subCategory(subCategory)
                            .topTalentExcelVersion(topTalentExcelVersion)
                            .lastUpdatedBy(principalFullName)
                            .lastUpdated(now)
                            .build();

                    if (practiceRatingRepository.findByUidAndSubCategoryAndTopTalentExcelVersion(uid, subCategory, topTalentExcelVersion).isEmpty()) {
                        employeeRating.setCreated(now);
                        employeeRating.setCreatedBy(principalFullName);
                    }

                    practiceRatingRepository.save(employeeRating);
                });

        meanRatingService.calculateMeanRating(uid, principalFullName, submissionStatus, topTalentExcelVersion);

        return getEmployeeRating(uid, customUserPrincipal);
    }

    @Override
    public List<String> getCompetencies() {
        log.info("Getting all competencies");

        return topTalentEmployeeRepository.findDistinctCompetencyPracticesByVersion(topTalentExcelVersionService.findLatestVersion());
    }

    @Override
    public PracticeRatingResponseDTO getCandidates(String email, String competency) {

        TopTalentExcelVersion topTalentExcelVersion = topTalentExcelVersionService.findLatestVersion();
        User user = userRepository.findByEmail(email).get();

        competency = ((user.getRole().getName()).equals(RoleConstants.PRACTICE)) ? user.getPractice() : competency;

        log.info("Getting employee list for the practice {}", competency);

        List<TopTalentEmployee> employees =
                competency.equals(Constants.DEFAULT_COMPETENCY) ?
                        topTalentEmployeeRepository.findAllByTopTalentExcelVersion(topTalentExcelVersion) :
                        topTalentEmployeeRepository.findByCompetencyPracticeAndTopTalentExcelVersion(competency, topTalentExcelVersion);

        Map<Long, UserProfile> userProfilesByUid = getUserProfileDetails(employees);

        List<PracticeEmployeeDTO> users = employees.stream()
                .map(employee -> getPracticeEmployeeDTO(employee, topTalentExcelVersion, userProfilesByUid.get(employee.getUid())))
                .toList();

        return PracticeRatingResponseDTO.builder()
                .competency(competency)
                .users(users)
                .build();
    }

    private PracticeEmployeeDTO getPracticeEmployeeDTO(TopTalentEmployee topTalentEmployee, TopTalentExcelVersion topTalentExcelVersion, UserProfile userProfile) {
            return topTalentEmployeeMapper.toPracticeEmployeeDTO(
                    userProfile,
                    topTalentEmployee,
                    meanRatingService.getSubmissionStatus(topTalentEmployee.getUid(),
                            topTalentExcelVersion),
                    meanRatingService.getMeanRating(topTalentEmployee.getUid(),
                            topTalentExcelVersion));
    }

    private Map<Long, UserProfile> getUserProfileDetails(List<TopTalentEmployee> employees)
    {
        Set<Long> uidSet = employees.stream()
                .map(TopTalentEmployee::getUid)
                .collect(Collectors.toSet());

        return userProfileService.fetchUserDetails(uidSet).stream()
                .collect(Collectors.toMap(UserProfile::getUid, userProfile -> userProfile));
    }

    @Override
    public boolean approveAllEmployeesRatings() {
        TopTalentExcelVersion topTalentExcelVersion = topTalentExcelVersionService.findLatestVersion();
        String email = CustomUserPrincipal.getLoggedInUser().getEmail();
        Optional<User> loggedInUser = userRepository.findByEmail(email);
        if (loggedInUser.isEmpty()) {
            throw new UserNotFoundException(ErrorMessages.USER_NOT_FOUND_WITH_EMAIL+email);
        }

        String practice = loggedInUser.get().getPractice();

        return meanRatingService.approveAllMeanRatings(topTalentExcelVersion, practice);
    }

}

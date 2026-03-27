package com.top.talent.management.service;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.MasterDataResponseDTO;
import com.top.talent.management.dto.TopTalentExcelVersionDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.entity.VersionStatus;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.mapper.ExcelVersionMapper;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.repository.VersionStatusRepository;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.TopTalentExcelVersionRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.MasterDataServiceImpl;
import com.top.talent.management.utils.TestUtils;
import com.top.talent.management.utils.TopTalentEmployeeTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.top.talent.management.constants.Constants.CULTURAL_SCORE;
import static com.top.talent.management.constants.Constants.HEROES;
import static com.top.talent.management.constants.Constants.STEP;
import static com.top.talent.management.constants.Constants.UNDERSCORE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MasterDataServiceImplTest {

    @InjectMocks
    private MasterDataServiceImpl masterDataService;

    @Mock
    private TopTalentExcelVersionService topTalentExcelVersionService;

    @Mock
    private TopTalentExcelVersionRepository topTalentExcelVersionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExcelVersionMapper excelVersionMapper;

    @Mock
    private VersionStatusRepository versionStatusRepository;

    @Mock
    private IdentificationClosureService identificationClosureService;

    @Mock
    private SuperAdminService superAdminService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private TopTalentEmployeeMapper topTalentEmployeeMapper;

    @Mock
    private MailGenerationService mailGenerationService;


    @Mock
    private TopTalentEmployeeRepository topTalentEmployeeRepository;

    private TopTalentEmployee employee1;
    private TopTalentEmployee employee2;
    private TopTalentExcelVersion mockExcelVersion;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);


         mockExcelVersion = new TopTalentExcelVersion();


         employee1 = TopTalentEmployee.builder()
                .name("John Doe")
                .uid(653006L)
                .topTalentExcelVersion(mockExcelVersion)
                .email("john.doe@example.com")
                .location("New York")
                .doj("2020-01-15")
                .timeWithEPAM("3 Years")
                .title("Software Engineer")
                .status("Active")
                .productionCategory("Development")
                .jobFunction("Engineering")
                .resourceManager("Jane Smith")
                .pgm("PGM123")
                .projectCode("PRJ456")
                .jfLevel("JF5")
                .competencyPractice("Java Practice")
                .primarySkill("Java")
                .nicheSkills("Spring, Hibernate")
                .nicheSkillYesNo("Yes")
                .talentProfilePreviousYear("High Performer")
                .talentProfile("Top Performer")
                .deliveryFeedbackTtScore(4.0)
                .practiceRating(null)
                .contributionEngXCulture(10L)
                .contributionExtraMiles(15L)
                .cultureScoreFromFeedback(80.0)
                .overallWeightedScoreForMerit(4.9)
                .ranking(1L)
                .percentile("98th Percentile")
                .hrbpMapping("HRBP1")
                .dh("John DH")
                .isStepUser(true)
                .build();



         employee2 = TopTalentEmployee.builder()
                .name("John Doe")
                .uid(653007L)
                .topTalentExcelVersion(mockExcelVersion)
                .email("john.doe@example.com")
                .location("New York")
                .doj("2020-01-15")
                .timeWithEPAM("3 Years")
                .title("Software Engineer")
                .status("Active")
                .productionCategory("Development")
                .jobFunction("Engineering")
                .resourceManager("Jane Smith")
                .pgm("PGM123")
                .projectCode("PRJ456")
                .jfLevel("JF5")
                .competencyPractice("Java Practice")
                .primarySkill("Java")
                .nicheSkills("Spring, Hibernate")
                .nicheSkillYesNo("Yes")
                .talentProfilePreviousYear("High Performer")
                .talentProfile("Top Performer")
                .deliveryFeedbackTtScore(95.0)
                .practiceRating(4.5)
                .contributionEngXCulture(10L)
                .contributionExtraMiles(15L)
                .cultureScoreFromFeedback(80.0)
                .overallWeightedScoreForMerit(4.8)
                .ranking(2L)
                .percentile("98th Percentile")
                .hrbpMapping("HRBP1")
                .dh("John DH")
                .isStepUser(true)
                .build();

    }

    @Test
    void testViewMasterData_WithValidFileName_ShouldReturnResponseDTO() {
        // Arrange
        String fileName = "STEP_2025_V1.xlsx";
        when(topTalentExcelVersionRepository.findByFileName(fileName)).thenReturn(Optional.of(mockExcelVersion));

        when((userProfileService.fetchUserDetails(Set.of()))).thenReturn(Set.of());

        when(excelVersionMapper.convertToDTO(any())).thenReturn(new TopTalentExcelVersionDTO());

        when(topTalentExcelVersionRepository.findAll()).thenReturn(Collections.emptyList());

        String cultureFile = "STEP_CULTURE_SCORE_2025_V1.xlsx";
        String heroesFile = "STEP_HEROES_2025_V1.xlsx";
        String versionName = "V1";
        String year = String.valueOf(LocalDateTime.now().getYear());
        TopTalentExcelVersion cultureVersion = TopTalentEmployeeTestUtils.createVersion(cultureFile, year, versionName);
        TopTalentExcelVersion heroesVersion = TopTalentEmployeeTestUtils.createVersion(heroesFile, year, versionName);


        when(topTalentExcelVersionService.getExcelVersionForYear("2025",STEP+UNDERSCORE+CULTURAL_SCORE+UNDERSCORE+"2025")).thenReturn(heroesVersion);

        when(topTalentExcelVersionService.getExcelVersionForYear("2025",STEP+UNDERSCORE+HEROES+UNDERSCORE+"2025")).thenReturn(cultureVersion);

        MasterDataResponseDTO responseDTO = masterDataService.viewMasterData(fileName);


        assertNotNull(responseDTO);
        verify(topTalentExcelVersionRepository, times(1)).findByFileName(fileName);
        verify(topTalentEmployeeRepository, times(2)).findAllByTopTalentExcelVersion(mockExcelVersion);
    }

    @Test
    void testViewMasterData_WithNullFileName_ShouldUseLatestVersion() {

        mockExcelVersion = new TopTalentExcelVersion("STEP_2025_V1.xlsx","V1", LocalDateTime.now(),"system",LocalDateTime.now(),"System","2025");
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(mockExcelVersion);
        when(topTalentExcelVersionRepository.findAll()).thenReturn(Collections.emptyList());

        String cultureFile = "STEP_CULTURE_SCORE_2025_V1.xlsx";
        String heroesFile = "STEP_HEROES_2025_V1.xlsx";
        String versionName = "V1";
        String year = String.valueOf(LocalDateTime.now().getYear());
        TopTalentExcelVersion cultureVersion = TopTalentEmployeeTestUtils.createVersion(cultureFile, year, versionName);
        TopTalentExcelVersion heroesVersion = TopTalentEmployeeTestUtils.createVersion(heroesFile, year, versionName);
        when(topTalentExcelVersionService.getExcelVersionForYear("2025",STEP+UNDERSCORE+HEROES+UNDERSCORE+"2025")).thenReturn(heroesVersion);

        when(topTalentExcelVersionService.getExcelVersionForYear("2025",STEP+UNDERSCORE+CULTURAL_SCORE+UNDERSCORE+"2025")).thenReturn(cultureVersion);


        MasterDataResponseDTO responseDTO = masterDataService.viewMasterData(null);

        assertNotNull(responseDTO);
        verify(topTalentExcelVersionService, times(1)).findLatestVersion();

    }

    @Test
    void testSaveEmployeesOfExcelVersion_ShouldSaveAndUpdateEmployees_henSubmissionStatusIsD() {
        // Arrange
        SubmissionStatus submissionStatus = SubmissionStatus.D;
        List<Long> uids = List.of(653006L, 653007L);

        mockExcelVersion = new TopTalentExcelVersion("STEP_2025_V1.xlsx","V1", LocalDateTime.now(),"system",LocalDateTime.now(),"System","2025");

        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(mockExcelVersion);

        List<TopTalentEmployee> mockEmployees = List.of(employee1,employee2);


        when(topTalentEmployeeRepository.findAllByUidInAndTopTalentExcelVersion(uids, mockExcelVersion))
                .thenReturn(mockEmployees);

        when(versionStatusRepository.findByTopTalentExcelVersion(mockExcelVersion))
                .thenReturn(Optional.of(new VersionStatus()));


        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();
        MasterDataResponseDTO responseDTO = masterDataService.saveEmployeesOfExcelVersion(submissionStatus, uids, customUserPrincipal);

        assertNotNull(responseDTO);
        verify(topTalentEmployeeRepository, times(2)).saveAll(anyList());
        verify(versionStatusRepository, times(1)).save(any(VersionStatus.class));
    }

    @Test
    void testSaveEmployeesOfExcelVersion_ShouldSaveAndUpdateEmployees_WhenSubmissionStatusIsS() {
        // Arrange
        SubmissionStatus submissionStatus = SubmissionStatus.S;
        List<Long> uids = List.of(653006L, 653007L);

      mockExcelVersion = new TopTalentExcelVersion("STEP_2025_V1.xlsx","V1", LocalDateTime.now(),"system",LocalDateTime.now(),"System","2025");

        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(mockExcelVersion);

        List<TopTalentEmployee> mockEmployees = List.of(employee1,employee2);

        when(topTalentEmployeeRepository.findAllByUidInAndTopTalentExcelVersion(uids, mockExcelVersion))
                .thenReturn(mockEmployees);

        when(versionStatusRepository.findByTopTalentExcelVersion(mockExcelVersion))
                .thenReturn(Optional.of(new VersionStatus()));
        when(superAdminService.grantAccessToUserRole()).thenReturn(List.of());


        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();

        doNothing().when(identificationClosureService).endPhase(customUserPrincipal);

        MasterDataResponseDTO responseDTO = masterDataService.saveEmployeesOfExcelVersion(submissionStatus, uids, customUserPrincipal);

        assertNotNull(responseDTO);
        verify(topTalentEmployeeRepository, times(2)).saveAll(anyList());
        verify(versionStatusRepository, times(1)).save(any(VersionStatus.class));
    }

    @Test
    void test_getUserProfile(){

        TopTalentExcelVersion excelVersion=new TopTalentExcelVersion();

        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(excelVersion);
        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(excelVersion)).thenReturn(List.of(employee1));

        UserProfile userProfile1=UserProfile.builder()
                .uid(12345L)
                .build();
        when(userProfileService.fetchUserDetails(Set.of(653006L))).thenReturn(Set.of(userProfile1));

        Map<Long, UserProfile> userProfile=masterDataService.getUserProfile();

        Assertions.assertEquals(Map.of(12345L,userProfile1),userProfile);
    }
}

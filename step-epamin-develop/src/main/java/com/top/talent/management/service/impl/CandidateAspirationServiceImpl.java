package com.top.talent.management.service.impl;

import com.top.talent.management.constants.AspirationPriority;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.AspirationApprovalRequestDTO;
import com.top.talent.management.dto.AspirationDTO;
import com.top.talent.management.dto.AspirationItemDTO;
import com.top.talent.management.dto.AspirationPriorityDTO;
import com.top.talent.management.dto.AspirationResponseDTO;
import com.top.talent.management.dto.SubmitAspirationsRequest;
import com.top.talent.management.entity.AspirationDetail;
import com.top.talent.management.entity.CandidateAspiration;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.CandidateAspirationException;
import com.top.talent.management.mapper.CandidateAspirationMapper;
import com.top.talent.management.repository.AspirationDetailRespository;
import com.top.talent.management.repository.CandidateAspirationRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.CandidateAspirationService;
import com.top.talent.management.service.TopTalentExcelVersionService;
import com.top.talent.management.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateAspirationServiceImpl implements CandidateAspirationService {

    private final UserService userService;

    private final TopTalentExcelVersionService topTalentExcelVersionService;

    private final CandidateAspirationRepository candidateAspirationRepository;

    private final AspirationDetailRespository aspirationDetailRespository;

    private final CandidateAspirationMapper mapper;


    @Override
    @Transactional
    public AspirationDTO saveAspiration(CustomUserPrincipal principal, AspirationDTO aspirationDTO) {

        Long uid = userService.getUuidByEmail(principal.getEmail());

        AspirationPriority priority = aspirationDTO.isAspiration1()
                ? AspirationPriority.ASPIRATION1 :
                AspirationPriority.ASPIRATION2;

        if (!aspirationDTO.isFutureSkillAcknowledged()) {
            log.error("User {} is attempting to create aspirations without acknowledging FutureSkills guidelines.", principal.getEmail());
            throw new CandidateAspirationException(ErrorMessages.ACKNOWLEDGMENT_BEFORE_CREATE_ASPIRATION);
        }


        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();
        List<CandidateAspiration> existingAspirations = candidateAspirationRepository
                .findByUidAndTopTalentExcelVersion(uid, latestVersion);

        validateExistingAspirations(existingAspirations, priority);

        List<CandidateAspiration> aspirations = buildCandidateAspirations(principal.getFullName(),
                                                                            aspirationDTO, uid, priority);
        log.info("Saving Aspirations: {} for user: {}", aspirations, principal.getEmail());

        List<CandidateAspiration> savedAspirations = candidateAspirationRepository.saveAll(aspirations);

        return AspirationDTO.builder()
                .aspiration1(aspirationDTO.isAspiration1())
                .aspirationList(savedAspirations.stream()
                        .map(mapper::mapToAspirationItemDTO)
                        .collect(Collectors.toList()))
                .futureSkillAcknowledged(true)
                .build();
    }


    @Override
    public AspirationPriorityDTO getAspirationByPriority(CustomUserPrincipal principal, AspirationPriority priority) {
        if(priority.equals(AspirationPriority.NA)){
           return buildEmptyAspirationDTO();
        }
        Long uid = userService.getUuidByEmail(principal.getEmail());
        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();

        List<CandidateAspiration> candidateAspirations=candidateAspirationRepository
                .findByUidAndPriorityAndTopTalentExcelVersion(uid,priority,latestVersion);

        if (candidateAspirations.isEmpty()) {
            log.warn("No aspirations found for user: {} with priority: {}", principal.getEmail(), priority);
            throw new CandidateAspirationException(ErrorMessages.ASPIRATION_NOT_FOUND+priority);
        }

        List<AspirationItemDTO> aspirationItems = candidateAspirations.stream()
                .map(mapper::mapToAspirationItemDTO)
                .collect(Collectors.toList());

        return AspirationPriorityDTO.builder()
                .aspirationList(aspirationItems)
                .priority(priority)
                .submissionStatus(candidateAspirations.get(0).getSubmissionStatus())
                .build();
    }


    @Override
    public AspirationDTO editAspiration(CustomUserPrincipal principal, AspirationPriority priority, AspirationDTO aspirationDTO) {

        Long uid = userService.getUuidByEmail(principal.getEmail());

        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();

        List<CandidateAspiration> existingAspirations = candidateAspirationRepository
                .findByUidAndPriorityAndTopTalentExcelVersion(uid, priority, latestVersion);

        if (existingAspirations.isEmpty()) {
            log.warn("No existing aspirations found for user: {}, priority: {}", principal.getEmail(), priority);
            throw new CandidateAspirationException(ErrorMessages.ASPIRATION_NOT_FOUND);
        }
        else if(existingAspirations.get(0).getSubmissionStatus().equals(SubmissionStatus.S)){
            log.error("User {} is attempting to modify submitted aspirations", CustomUserPrincipal.getLoggedInUser());
            throw new CandidateAspirationException(ErrorMessages.ASPIRATIONS_SUBMITTED);
        }

        Map<String, CandidateAspiration> existingAspirationsMap = existingAspirations.stream()
                .collect(Collectors.toMap(
                        aspiration -> aspiration.getAspirationDetail().getTitle(),
                        aspiration -> aspiration
                ));

        aspirationDTO.getAspirationList().stream() .map(aspirationItemDTO ->
                existingAspirationsMap.get(aspirationItemDTO.getTitle()))
                .filter(Objects::nonNull)
                .forEach(existingAspiration ->
                        validatePriorityChange(existingAspiration, aspirationDTO));

        Map<String, String> inputValuesMap = aspirationDTO.getAspirationList().stream()
                .collect(Collectors.toMap(AspirationItemDTO::getTitle, AspirationItemDTO::getInputValue));

        existingAspirations
                .forEach(aspiration ->
                { String inputValue = inputValuesMap.get(aspiration.getAspirationDetail().getTitle());
                    if (inputValue != null) {
                        aspiration.setInputValue(inputValue);
                        aspiration.setLastUpdated(LocalDateTime.now());
                        aspiration.setLastUpdatedBy(principal.getFullName());
                    }
                });

        List<CandidateAspiration> savedAspirations = candidateAspirationRepository.saveAll(existingAspirations);

        return AspirationDTO.builder()
                .aspiration1(aspirationDTO.isAspiration1())
                .aspirationList(savedAspirations.stream()
                        .map(mapper::mapToAspirationItemDTO)
                        .collect(Collectors.toList()))
                .build();
    }


    @Override
    @Transactional
    public AspirationDTO deleteAspiration(CustomUserPrincipal principal, AspirationPriority priority) {

        Long uid = userService.getUuidByEmail(principal.getEmail());

        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();


        List<CandidateAspiration> aspirationsToDelete = candidateAspirationRepository
                .findByUidAndPriorityAndTopTalentExcelVersion(uid, priority, latestVersion);

        if (aspirationsToDelete.isEmpty()) {
            log.warn("No aspirations to delete for user: {} with priority: {}", principal.getEmail(), priority);
            throw new CandidateAspirationException(ErrorMessages.ASPIRATION_NOT_FOUND+priority);
        }
        else if(aspirationsToDelete.get(0).getSubmissionStatus().equals(SubmissionStatus.S)){
            log.error("User {} is attempting to delete submitted aspirations", principal.getEmail());
            throw new CandidateAspirationException(ErrorMessages.ASPIRATIONS_SUBMITTED);
        }
         candidateAspirationRepository.deleteAll(aspirationsToDelete);

        List<AspirationItemDTO> deletedAspirations= aspirationsToDelete.stream()
                        .map(mapper::mapToAspirationItemDTO)
                                .toList();
        boolean isAspiration= AspirationPriority.ASPIRATION1.equals(priority);
        log.info("Deleted aspiration(s) with priority: {}", priority);
        return AspirationDTO.builder()
                .aspiration1(isAspiration)
                .aspirationList(deletedAspirations)
                .build();

    }

    @Override
    public AspirationResponseDTO getAspirations(CustomUserPrincipal principal) {
        Long uid = userService.getUuidByEmail(principal.getEmail());
        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();
        List<AspirationPriorityDTO> currentYearAspirations = getAspirationsForExcelVersion(uid, latestVersion);
        TopTalentExcelVersion previousYearVersion = topTalentExcelVersionService.getPreviousYearVersion();

        List<AspirationPriorityDTO> previousYearAspirations = getAspirationsForExcelVersion(uid, previousYearVersion);
        boolean isFormActive = checkIfFormActive(currentYearAspirations);

        boolean isFutureSkillAcknowledgment = candidateAspirationRepository
                .findByUidAndTopTalentExcelVersion(uid,latestVersion)
                .stream()
                        .anyMatch(CandidateAspiration::isFutureSkillAcknowledged);
        log.info("Fetched FutureSkillAcknowledgment={} for the UID={}",isFutureSkillAcknowledgment, uid);

        boolean isAcknowledgment = candidateAspirationRepository
                .findByUidAndTopTalentExcelVersion(uid, latestVersion)
                .stream()
                        .anyMatch(CandidateAspiration::isSubmitAcknowledged);
        log.info("Fetched acknowledgment={} for UID={}", isAcknowledgment, uid);


        List<String> aspirationExplanation = aspirationDetailRespository.findAll().stream()
                .map(aspirationDetail ->
                        aspirationDetail.getTitle() +": "+ aspirationDetail.getDescription())
                .toList();

        return AspirationResponseDTO.builder()
                .isFormActive(isFormActive)
                .aspirationExplanation(aspirationExplanation)
                .aspirations(currentYearAspirations)
                .previousYearAspirations(previousYearAspirations)
                .futureSkillAcknowledgment(isFutureSkillAcknowledgment)
                .submitAcknowledgment(isAcknowledgment)
                .build();
    }

    @Override
    public List<AspirationDTO> submitAspirations(CustomUserPrincipal principal, SubmitAspirationsRequest submitRequest) {

        if (!submitRequest.isSubmitAcknowledged()) {
            log.error("Submission failed: Acknowledgment is missing for user {}", principal.getEmail());
            throw new CandidateAspirationException(ErrorMessages.ACKNOWLEDGMENT_BEFORE_SUBMIT);
        }

        Long uid = userService.getUuidByEmail(principal.getEmail());
        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();
        List<CandidateAspiration> draftAspirations = candidateAspirationRepository
                .findByUidAndTopTalentExcelVersion(uid, latestVersion);

        if(draftAspirations.stream().anyMatch(aspiration->aspiration.getSubmissionStatus()==SubmissionStatus.S)){
            log.error("User {} is attempting to submit already submitted aspirations", principal.getEmail());
            throw new CandidateAspirationException(ErrorMessages.ASPIRATIONS_SUBMITTED);
        }


        boolean hasP1 = draftAspirations.stream().anyMatch(aspiration ->
                AspirationPriority.ASPIRATION1.equals(aspiration.getPriority()));

        boolean hasP2 = draftAspirations.stream().anyMatch(aspiration ->
                AspirationPriority.ASPIRATION2.equals(aspiration.getPriority()));

        if(!hasP1 && !hasP2){
            log.warn("User {} is attempting to submit without P1 or P2 aspirations", principal.getEmail());
            throw new CandidateAspirationException(ErrorMessages.INCOMPLETE_ASPIRATIONS);
        }

        if(hasP1 && hasP2 ){
            draftAspirations.forEach(aspiration -> {
                aspiration.setSubmissionStatus(SubmissionStatus.S);
                aspiration.setSubmitAcknowledged(true);
                aspiration.setLastUpdated(LocalDateTime.now());
                aspiration.setLastUpdatedBy(principal.getFullName());
            });
            List<CandidateAspiration> aspirations = candidateAspirationRepository.saveAll(draftAspirations);

            log.info("Aspirations submitted successfully by user: {}", principal.getEmail());

            List<AspirationItemDTO> priority1 = aspirations.stream()
                    .filter(aspiration -> AspirationPriority.ASPIRATION1.equals(aspiration.getPriority()))
                    .toList()
                    .stream()
                    .map(mapper::mapToAspirationItemDTO)
                    .toList();

            List<AspirationItemDTO> priority2 = aspirations.stream()
                    .filter(aspiration -> AspirationPriority.ASPIRATION2.equals(aspiration.getPriority()))
                    .toList()
                    .stream()
                    .map(mapper::mapToAspirationItemDTO)
                    .toList();

            return List.of(
                    AspirationDTO.builder()
                            .aspiration1(true)
                            .aspirationList(priority1)
                            .futureSkillAcknowledged(true)
                            .build(),
                    AspirationDTO.builder()
                            .aspiration1(false)
                            .aspirationList(priority2)
                            .futureSkillAcknowledged(true)
                            .build()
            );
        }else{
            log.error("Incomplete aspirations submission attempt by user: {}", principal.getEmail());
            throw new CandidateAspirationException(ErrorMessages.INCOMPLETE_ASPIRATIONS);
        }
    }

    private List<AspirationPriorityDTO> getAspirationsForExcelVersion(Long uid,
                                                                      TopTalentExcelVersion excelVersion) {
        log.info("Fetching aspirations for user: {} and excel version: {}", uid, excelVersion);
        if(Objects.equals(excelVersion.getVersionName(), "NA")){
            return List.of();
        }
        List<CandidateAspiration> aspirations = candidateAspirationRepository
                                                    .findByUidAndTopTalentExcelVersion(uid, excelVersion);

        Map<AspirationPriority, List<AspirationItemDTO>> groupedAspirations = aspirations.stream()
                .collect(Collectors.groupingBy(
                        CandidateAspiration::getPriority,
                        Collectors.mapping(mapper::mapToAspirationItemDTO, Collectors.toList())
                ));

        return groupedAspirations.entrySet().stream()
                .map(entry -> AspirationPriorityDTO.builder()
                                                    .priority(entry.getKey())
                                                    .aspirationList(entry.getValue())
                                                    .submissionStatus(aspirations.get(0).getSubmissionStatus())
                                                    .build())
                .collect(Collectors.toList());
    }


    private List<CandidateAspiration> buildCandidateAspirations(String name, AspirationDTO aspirationDTO
                                                                , Long uid, AspirationPriority priority) {

        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();
        List<String> titles = aspirationDTO.getAspirationList().stream()
                .map(AspirationItemDTO::getTitle)
                .collect(Collectors.toList());

        Map<String, AspirationDetail> aspirationDetailMap = aspirationDetailRespository.findByTitleIn(titles)
                .stream().collect(Collectors.toMap(AspirationDetail::getTitle, detail -> detail));

        return aspirationDTO.getAspirationList().stream().map(item -> {

            AspirationDetail aspirationDetail = aspirationDetailMap.get(item.getTitle());

            return CandidateAspiration.builder()
                    .uid(uid)
                    .topTalentExcelVersion(latestVersion)
                    .priority(priority)
                    .aspirationDetail(aspirationDetail)
                    .inputValue(item.getInputValue())
                    .futureSkillAcknowledged(true)
                    .submissionStatus(SubmissionStatus.D)
                    .created(LocalDateTime.now())
                    .createdBy(name)
                    .lastUpdated(LocalDateTime.now())
                    .lastUpdatedBy(name)
                    .build();
        }).collect(Collectors.toList());
    }

    private void validatePriorityChange(CandidateAspiration existingAspiration, AspirationDTO aspirationDTO) {

        AspirationPriority incomingPriority = aspirationDTO.isAspiration1()
                ? AspirationPriority.ASPIRATION1
                : AspirationPriority.ASPIRATION2;


        if (!existingAspiration.getPriority().equals(incomingPriority)) {
            log.error("Priority modification not allowed for aspiration: {}. Existing: {}, New: {} for user {}"
                    , existingAspiration.getAspirationDetail().getTitle(), existingAspiration.getPriority()
                    , incomingPriority, CustomUserPrincipal.getLoggedInUser().getEmail());
            throw new CandidateAspirationException(ErrorMessages.PRIORITY_MODIFICATION_NOT_ALLOWED);
        }
    }

    private void validateExistingAspirations(List<CandidateAspiration> existingAspirations
                                            , AspirationPriority priority) {
        String email = CustomUserPrincipal.getLoggedInUser().getEmail();

        if(!existingAspirations.isEmpty() &&
                existingAspirations.get(0).getSubmissionStatus().equals(SubmissionStatus.S)){
            log.error(ErrorMessages.ASPIRATIONS_SUBMITTED + " for user {}", email);
            throw new CandidateAspirationException(ErrorMessages.ASPIRATIONS_SUBMITTED);
        }

        boolean hasPriority1 = existingAspirations.stream()
                .anyMatch(aspiration -> AspirationPriority.ASPIRATION1.equals(aspiration.getPriority()));
        boolean hasPriority2 = existingAspirations.stream()
                .anyMatch(aspiration -> AspirationPriority.ASPIRATION2.equals(aspiration.getPriority()));


       if (hasPriority1 && hasPriority2) {
            log.error(ErrorMessages.ASPIRATION_LIMIT_EXCEEDED + " for user {}", email);
            throw new CandidateAspirationException(ErrorMessages.ASPIRATION_LIMIT_EXCEEDED);
        }
        else if (hasPriority1 && AspirationPriority.ASPIRATION1.equals(priority)) {
            log.error("User attempted to create another aspiration1 while one already exists.{}",email);
            throw new CandidateAspirationException(ErrorMessages.CREATE_ASPIRATION2);
        }
        else if (hasPriority2 && AspirationPriority.ASPIRATION2.equals(priority)) {
            log.error("User attempted to create another aspiration2 while one already exists. {}",email);
            throw new CandidateAspirationException(ErrorMessages.CREATE_ASPIRATION1);
        }
        log.info("Aspiration validation passed successfully for user{}", email);
    }


    private boolean checkIfFormActive(List<AspirationPriorityDTO> currentYearAspirations){
        if(currentYearAspirations.isEmpty()){
            return true;
        }
        return !currentYearAspirations.stream()
                .allMatch(aspiration ->
                        SubmissionStatus.S.equals(aspiration.getSubmissionStatus()));
    }

    private AspirationPriorityDTO buildEmptyAspirationDTO(){
        List<AspirationDetail> aspirationDetails = aspirationDetailRespository.findAll();
        return AspirationPriorityDTO.builder()
                .aspirationList(aspirationDetails.stream()
                        .map(detail -> AspirationItemDTO.builder()
                                .title(detail.getTitle())
                                .description(detail.getDescription())
                                .build())
                        .collect(Collectors.toList()))
                .priority(AspirationPriority.ASPIRATION2)
                .build();
    }

    @Override
    public List<AspirationResponseDTO> getSubmittedAspirationsForApproval() {
        List<CandidateAspiration> submittedAspirations =
                candidateAspirationRepository.findBySubmissionStatus(SubmissionStatus.S);

        return submittedAspirations.stream()
                .map(mapper::mapToAspirationResponseDTO)
                .toList();
    }

    @Transactional
    public void approveAspiration(Long uid, Long aspirationDetailId, AspirationApprovalRequestDTO approvalRequest) {
        CandidateAspiration aspiration = getCandidateAspiration(uid, aspirationDetailId);

        aspiration.setAssignedRole(approvalRequest.getAssignedRole());
        aspiration.setProficiency(approvalRequest.getProficiency());
        aspiration.setSubmissionStatus(SubmissionStatus.A);
        aspiration.setApprovedBy(approvalRequest.getApprovedBy());
        aspiration.setLastUpdated(LocalDateTime.now());

        candidateAspirationRepository.save(aspiration);

        log.info("Aspiration approved successfully for Candidate ID: {}, Aspiration Detail ID: {}",
                uid, aspirationDetailId);
    }

    private CandidateAspiration getCandidateAspiration(Long uid, Long aspirationDetailId) {
        return candidateAspirationRepository
                .findByUidAndAspirationDetailId(uid, aspirationDetailId)
                .orElseThrow(() -> new CandidateAspirationException("Aspiration not found for the given candidate and detail ID."));
    }

}


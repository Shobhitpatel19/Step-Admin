package com.top.talent.management.service.impl;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.entity.IdentificationClosure;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.repository.IdentificationClosureRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.IdentificationClosureService;
import com.top.talent.management.service.TopTalentExcelVersionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentificationClosureServiceImpl implements IdentificationClosureService {
    private final IdentificationClosureRepository idPhaseRepository;
    private final TopTalentExcelVersionService topTalentExcelVersionService;

    @Override
    @Transactional
    public void endPhase(CustomUserPrincipal customUserPrincipal){
        TopTalentExcelVersion topTalentExcelVersion = topTalentExcelVersionService.findLatestVersion();
        IdentificationClosure idPhase = IdentificationClosure.builder()
                .topTalentExcelVersion(topTalentExcelVersion)
                .endedBy(customUserPrincipal.getEmail())
                .endedAt(LocalDateTime.now())
                .build();
        log.info("Ending phase: {} {}", idPhase.getTopTalentExcelVersion().getVersionName(), idPhase.getTopTalentExcelVersion().getUploadedYear());
        idPhaseRepository.save(idPhase);
    }

    @Override
    public TopTalentExcelVersion findLatestPhase(){
        List<IdentificationClosure> phases = idPhaseRepository.findAll();
        if(phases.isEmpty()){
            throw new VersionException(ErrorMessages.NO_PHASES_FOUND);
        }
        IdentificationClosure latestPhase = phases.stream().max(Comparator.comparing(IdentificationClosure::getEndedAt))
                .orElseThrow(() -> new VersionException(ErrorMessages.LIST_NOT_FOUND));

        log.info("Latest phase: {}", latestPhase);
        return latestPhase.getTopTalentExcelVersion();
    }

    @Override
    public boolean isPhaseClosed() throws VersionException{
        TopTalentExcelVersion idPhaseVersion = findLatestPhase();
        TopTalentExcelVersion topTalentExcelVersion = topTalentExcelVersionService.findLatestVersion();
        return topTalentExcelVersion.equals(idPhaseVersion);
    }

    @Override
    public LocalDateTime latestPhaseEndDate(){
        List<IdentificationClosure> phases = idPhaseRepository.findAll();

        IdentificationClosure latestPhase = phases.stream().max(Comparator.comparing(IdentificationClosure::getEndedAt))
                .orElseThrow(() -> new VersionException(ErrorMessages.LIST_NOT_FOUND));

        log.info("Latest phase: {}", latestPhase);
        return latestPhase.getEndedAt();

    }

    @Override
    public boolean isFormClosed(int noOfWeeks) {
        if (isPhaseClosed()) {
            LocalDate current = LocalDateTime.now().toLocalDate();
            LocalDate phaseEndDate = latestPhaseEndDate().toLocalDate();
            LocalDate plus = phaseEndDate.plusWeeks(noOfWeeks);
            return !plus.isEqual(current) && plus.isBefore(current);
        }
        return false;
    }
    @Override
    public boolean isIdentificationClosureDataPresent() {
        boolean exists = idPhaseRepository.count() > 0;
        log.info("IdentificationClosureRepository contains records: {}", exists);
        return exists;
    }
}

package com.top.talent.management.service;

import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.security.CustomUserPrincipal;

import java.time.LocalDateTime;

public interface IdentificationClosureService {
    void endPhase(CustomUserPrincipal customUserPrincipal);
    TopTalentExcelVersion findLatestPhase();
    boolean isPhaseClosed();
    LocalDateTime latestPhaseEndDate();
    boolean isFormClosed(int noOfWeeks);
    boolean isIdentificationClosureDataPresent();
    }

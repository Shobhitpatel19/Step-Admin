package com.top.talent.management.service;

import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.EmployeeRatingRequestDTO;
import com.top.talent.management.dto.EmployeeRatingResponseDTO;
import com.top.talent.management.dto.PracticeRatingResponseDTO;
import com.top.talent.management.security.CustomUserPrincipal;

import java.util.List;

public interface PracticeRatingService {

    EmployeeRatingResponseDTO getEmployeeRating(Long uid, CustomUserPrincipal customUserPrincipal);

    EmployeeRatingResponseDTO saveEmployeeRatings(Long uid, SubmissionStatus submissionStatus, EmployeeRatingRequestDTO request, CustomUserPrincipal customUserPrincipal);

    PracticeRatingResponseDTO getCandidates(String email, String practice);

    List<String> getCompetencies();

    boolean approveAllEmployeesRatings();
}

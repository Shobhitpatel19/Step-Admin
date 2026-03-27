package com.top.talent.management.service;

import com.top.talent.management.constants.AspirationPriority;
import com.top.talent.management.dto.AspirationApprovalRequestDTO;
import com.top.talent.management.dto.AspirationDTO;
import com.top.talent.management.dto.AspirationPriorityDTO;
import com.top.talent.management.dto.AspirationResponseDTO;
import com.top.talent.management.dto.SubmitAspirationsRequest;
import com.top.talent.management.security.CustomUserPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CandidateAspirationService {

    AspirationDTO saveAspiration(CustomUserPrincipal principal, AspirationDTO aspirationDTO);

    AspirationPriorityDTO getAspirationByPriority(CustomUserPrincipal principal, AspirationPriority priority);

    AspirationDTO editAspiration(CustomUserPrincipal principal, AspirationPriority priority, AspirationDTO aspirationDTO);

    AspirationDTO deleteAspiration(CustomUserPrincipal principal, AspirationPriority priority);

    AspirationResponseDTO getAspirations(CustomUserPrincipal principal);

    List<AspirationDTO> submitAspirations(CustomUserPrincipal principal, SubmitAspirationsRequest submitRequest);

    List<AspirationResponseDTO> getSubmittedAspirationsForApproval();

    void approveAspiration(Long candidateId, Long aspirationDetailId, AspirationApprovalRequestDTO approvalRequest);
}


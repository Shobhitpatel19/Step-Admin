package com.top.talent.management.service.impl;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.InvalidCandidateException;
import com.top.talent.management.exception.PracticeDelegationException;
import com.top.talent.management.exception.PracticeRatingException;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.ValidateCandidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ValidateCandidateServiceImpl implements ValidateCandidateService {

    private final TopTalentEmployeeRepository topTalentEmployeeRepository;

    private final UserRepository userRepository;

    @Override
    public TopTalentEmployee isValidCandidate(Long uid, CustomUserPrincipal customUserPrincipal, TopTalentExcelVersion topTalentExcelVersion) {

        TopTalentEmployee topTalentEmployee = topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion)
                .orElseThrow(() -> {
                    log.info("Employee with uid {} does not exist in table step_top_talent_employees, for the year {} with version {}",
                            uid, topTalentExcelVersion.getUploadedYear(), topTalentExcelVersion.getVersionName());
                    return new PracticeRatingException(ErrorMessages.CANDIDATE_DOES_NOT_EXIST);
                });


        User loggedInUser = userRepository.findByEmail(customUserPrincipal.getEmail()).get();

        log.info("User has role{}: ", customUserPrincipal.getRole());

        if(customUserPrincipal.getRole().equals(RoleConstants.ROLE_SUPER_ADMIN))
        {
            return topTalentEmployee;
        }
        else if(!topTalentEmployee.getCompetencyPractice().equalsIgnoreCase(loggedInUser.getPractice()))
        {
            log.info("Employee with uid {} is not a part of practice {}", uid, loggedInUser.getPractice());

            throw new PracticeRatingException(ErrorMessages.INVALID_CANDIDATE);
        }

        return topTalentEmployee;
    }
}

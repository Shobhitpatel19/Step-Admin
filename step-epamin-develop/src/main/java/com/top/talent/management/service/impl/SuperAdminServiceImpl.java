package com.top.talent.management.service.impl;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.mapper.UserMapper;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.service.SuperAdminService;
import com.top.talent.management.service.TopTalentExcelVersionService;
import com.top.talent.management.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.top.talent.management.constants.Constants.EMPTY_USER_LIST;
import static com.top.talent.management.constants.Constants.USER_STATUS_INACTIVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuperAdminServiceImpl implements SuperAdminService {

    private final TopTalentEmployeeRepository topTalentEmployeeRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final TopTalentExcelVersionService topTalentExcelVersionService;

    @Override
    @Transactional
    public List<UserDTO> grantAccessToUserRole() {

        TopTalentExcelVersion excelVersion = topTalentExcelVersionService.findLatestVersion();

        List<TopTalentEmployee> stepUsers = topTalentEmployeeRepository.findAllByTopTalentExcelVersionAndIsStepUser(excelVersion,true);

        if(stepUsers.isEmpty()){
            log.info("NO active step users found.");
            return EMPTY_USER_LIST;
        }
        else {

            Role role = roleRepository.findByName(RoleConstants.USER);

            List<User> users = userRepository.findAllByRole(role);
            users.forEach(user -> user.setStatus(USER_STATUS_INACTIVE));

            userRepository.saveAll(users);

            List<User> savedUsers = userService.addEligibleUsers(stepUsers);

            return savedUsers.stream().map(userMapper::toUserDTO).toList();
        }
    }
}

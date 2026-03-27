package com.top.talent.management.helper;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.User;
import com.top.talent.management.mapper.UserMapper;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.top.talent.management.constants.Constants.USER_STATUS_ACTIVE;

@RequiredArgsConstructor
@Component
public class SuperAdminServiceHelper {

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    public User generateUserObject(TopTalentEmployee topTalentEmployee) {
        User user = userMapper.toUser(topTalentEmployee);

        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Role role = roleRepository.findByName(RoleConstants.USER);

        String[] firstAndLastNameArr = getFirstAndLastName(topTalentEmployee.getName());

        user.setFirstName(firstAndLastNameArr[0]);
        user.setLastName(firstAndLastNameArr[1]);
        user.setStatus(USER_STATUS_ACTIVE);
        user.setCreated(LocalDateTime.now());
        user.setCreatedBy(userPrincipal.getFullName());
        user.setLastUpdated(LocalDateTime.now());
        user.setLastUpdatedBy(userPrincipal.getFullName());
        user.setRole(role);
        user.setDelegate(false);
        return user;
    }

    public String[] getFirstAndLastName(String fullName) {
        String[] firstAndLastNameArr = new String[2];

        int spaceLastIndex = fullName.lastIndexOf(" ");

        if (spaceLastIndex == -1) {
            firstAndLastNameArr[0] = fullName;
            firstAndLastNameArr[1] = "";
            return firstAndLastNameArr;
        }
        firstAndLastNameArr[0] = fullName.substring(0, spaceLastIndex);
        firstAndLastNameArr[1] = fullName.substring(spaceLastIndex + 1);
        return firstAndLastNameArr;
    }
}


package com.top.talent.management.service;

import com.top.talent.management.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SuperAdminService {
    List<UserDTO> grantAccessToUserRole();

}

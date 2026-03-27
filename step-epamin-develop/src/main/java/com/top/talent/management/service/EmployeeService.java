package com.top.talent.management.service;

import com.top.talent.management.dto.EmployeeDTO;

public interface EmployeeService {
    EmployeeDTO getEmployeeProfile(String email);
}

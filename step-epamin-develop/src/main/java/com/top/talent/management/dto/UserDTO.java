package com.top.talent.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Data
@SuperBuilder
@AllArgsConstructor
public class UserDTO {

    private Long uuid;

    private String email;

    private String firstName;

    private String lastName;

    private String practice;

    private boolean isDelegate;

    private String roleName;

    private String isActive;
}

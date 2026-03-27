package com.top.talent.management.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class UserResponseDTO extends UserDTO{

    private String status;

    private String message;

}

package com.top.talent.management.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class UserProfile {
    private String fullName;
    private String firstName;
    private String lastName;
    private String photo;
    private String jobDesignation;
    private String officeAddress;
    private Long uid;
    private String email;
    private String primarySkill;
    private String jobLevel;
    private String jobTrack;
    private String jobTrackLevel;
    private String employmentId;
    private String unit;
    private String profileType;
    private String lastPromotionDate;


}
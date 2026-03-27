package com.top.talent.management.controller;

import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.service.UserProfileService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.top.talent.management.constants.RoleConstants.HAS_ANY_ROLE_SA_P;
import static com.top.talent.management.constants.RoleConstants.HAS_ROLE_SUPER_ADMIN;

@RequiredArgsConstructor
@RestController
@RequestMapping("/step")
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/full-user-profile")
    @PreAuthorize(HAS_ANY_ROLE_SA_P)
    public ResponseEntity<Set<UserProfile>> searchEmployee(@PathParam("name") String name){
        return ResponseEntity.ok(userProfileService.fetchUserDetails(name));
    }

    @GetMapping("/full-user-profile-above-b3")
    @PreAuthorize(HAS_ANY_ROLE_SA_P)
    public ResponseEntity<Set<UserProfile>> searchEmployeeAboveB3(@PathParam("name") String name){
        return ResponseEntity.ok(userProfileService.fetchUserDetailsAboveB3(name));
    }

    @GetMapping("/all-practice-heads")
    @PreAuthorize(HAS_ROLE_SUPER_ADMIN)
    public ResponseEntity<Set<UserProfile>> getPracticeHeads(){
        return ResponseEntity.ok(userProfileService.getPracticeHeads());
    }

}


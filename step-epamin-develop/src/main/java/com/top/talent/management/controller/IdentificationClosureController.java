package com.top.talent.management.controller;

import com.top.talent.management.service.IdentificationClosureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class IdentificationClosureController {

    private final IdentificationClosureService identificationClosureService;

    @GetMapping("/step/identification/isended")
    public ResponseEntity<Map<String,Boolean>> isIdentificationPhaseEnded() {
        return ResponseEntity.ok(Map.of("status", identificationClosureService.isPhaseClosed()));
    }


}


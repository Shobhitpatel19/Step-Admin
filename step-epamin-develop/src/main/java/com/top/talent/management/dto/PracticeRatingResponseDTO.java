package com.top.talent.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PracticeRatingResponseDTO {

    String competency;

    List<PracticeEmployeeDTO> users;
}

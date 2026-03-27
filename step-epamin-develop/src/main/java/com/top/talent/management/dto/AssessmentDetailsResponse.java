package com.top.talent.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AssessmentDetailsResponse {

    private int total;
    private List<Result> results;

    @Data
    public static class Result {

        @JsonProperty("_key")
        private String key;

        @JsonProperty("_partition")
        private int partition;

        @JsonProperty("_offset")
        private int offset;

        @JsonProperty("_operation")
        private String operation;

        @JsonProperty("approval_change_date")
        private String approvalChangeDate;

        @JsonProperty("approver_id")
        private String approverId;

        @JsonProperty("assessment_result")
        private String assessmentResult;

        @JsonProperty("assessment_type_id")
        private int assessmentTypeId;

        @JsonProperty("asssessment_type_name")
        private String assessmentTypeName;

        private String candidate;

        @JsonProperty("candidate_id")
        private String candidateId;

        @JsonProperty("candidate_representative_id")
        private String candidateRepresentativeId;

        private String committee;

        @JsonProperty("committee_id")
        private String committeeId;

        @JsonProperty("current_job_function")
        private String currentJobFunction;

        @JsonProperty("current_job_function_id")
        private String currentJobFunctionId;

        @JsonProperty("current_primary_skill")
        private String currentPrimarySkill;

        @JsonProperty("current_title_name")
        private String currentTitleName;

        private String discipline;

        @JsonProperty("discipline_id")
        private int disciplineId;

        @JsonProperty("discipline_name")
        private String disciplineName;

        @JsonProperty("draft_prolonged_till_date")
        private String draftProlongedTillDate;

        @JsonProperty("feedback_provided_date")
        private String feedbackProvidedDate;

        @JsonProperty("frozen_on")
        private String frozenOn;

        @JsonProperty("head_id")
        private String headId;

        @JsonProperty("hr_id")
        private String hrId;

        @JsonProperty("is_active")
        private boolean isActive;

        @JsonProperty("is_debt_request")
        private boolean isDebtRequest;

        @JsonProperty("is_hr_required")
        private boolean isHrRequired;

        @JsonProperty("is_native")
        private boolean isNative;

        @JsonProperty("is_reassessment")
        private boolean isReassessment;

        private String level;

        @JsonProperty("locaiton_id")
        private String locationId;

        @JsonProperty("next_job_function_base")
        private String nextJobFunctionBase;

        @JsonProperty("next_job_function_base_id")
        private String nextJobFunctionBaseId;

        @JsonProperty("next_primary_skill_id")
        private String nextPrimarySkillId;

        @JsonProperty("next_primary_skill_name")
        private String nextPrimarySkillName;

        @JsonProperty("next_title_id")
        private String nextTitleId;

        @JsonProperty("next_title_name")
        private String nextTitleName;

        @JsonProperty("optimal_session_month")
        private String optimalSessionMonth;

        private String period;

        @JsonProperty("period_id")
        private String periodId;

        @JsonProperty("request_id")
        private String requestId;

        @JsonProperty("session_date_time")
        private String sessionDateTime;

        @JsonProperty("session_preferable_month")
        private String sessionPreferableMonth;

        @JsonProperty("session_preferable_month_id")
        private Integer sessionPreferableMonthId;

        private String status;

        @JsonProperty("status_id")
        private int statusId;

        @JsonProperty("title_line_id")
        private String titleLineId;

        @JsonProperty("urgency_comment")
        private String urgencyComment;

        @JsonProperty("urgency_id")
        private Integer urgencyId;

        @JsonProperty("urgency_name")
        private String urgencyName;
    }
}

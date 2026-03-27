package com.top.talent.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApiProfileResponse {

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

        private Entity entity;

        private List<Employments> employments;

        @JsonProperty("business_name")
        private BusinessName businessName;

        @JsonProperty("external_ids")
        private List<ExternalId> externalIds;

        @JsonProperty("employment_id")
        private String employmentId;

        @JsonProperty("hrms_global_user_id")
        private String hrmsGlobalUserId;

        @JsonProperty("public_id")
        private String publicId;

        private String uid;

        @JsonProperty("native_full_name")
        private String nativeFullName;

        @JsonProperty("photo_url")
        private String photoUrl;

        @JsonProperty("job_title")
        private JobTitle jobTitle;

        @JsonProperty("worksite_location")
        private WorksiteLocation worksiteLocation;

        @JsonProperty("business_email")
        private String businessEmail;

        @JsonProperty("primary_skill")
        private PrimarySkill primarySkill;

        @JsonProperty("job_function")
        private JobFunction jobFunction;

        @JsonProperty("job_function_effective_from")
        private String jobFunctionEffectiveFrom;

        private Unit unit;

        @JsonProperty("resource_manager")
        private ResourceManager resourceManager;

        @JsonProperty("job_function_track")
        private String jobFunctionTrack;

        @JsonProperty("job_function_level")
        private String jobFunctionLevel;

        @JsonProperty("profile_type")
        private ProfileType profileType;

        @Data
        public static class Entity {

            private String id;

            @JsonProperty("is_active")
            private boolean isActive;

            @JsonProperty("created_when")
            private String createdWhen;

            @JsonProperty("updated_when")
            private String updatedWhen;

            @JsonProperty("replaced_with_id")
            private String replacedWithId;
        }

        @Data
        public static class Employments {

            @JsonProperty("employment_id")
            private String employmentId;

            private boolean active;
        }

        @Data
        public static class ExternalId {

            private String id;
            private Type type;

            @Data
            public static class Type {
                private String id;
                private String name;
            }
        }

        @Data
        public static class JobTitle {
            private String id;
            private String name;
        }

        @Data
        public static class WorksiteLocation {
            private String id;
            private String name;
        }

        @Data
        public static class PrimarySkill {
            private String id;
            private String name;
        }

        @Data
        public static class JobFunction {
            private String id;
            private String name;
        }

        @Data
        public static class Unit {
            private String id;
            private String name;
        }

        @Data
        public static class ResourceManager {
            private String id;
            private String name;
        }

        @Data
        public static class ProfileType {
            private String id;
            private String name;
            private String type;
        }

        @Data
        public static class BusinessName {
            @JsonProperty("first_name")
            private String firstName;
            @JsonProperty("last_name")
            private String lastName;
        }
    }
}

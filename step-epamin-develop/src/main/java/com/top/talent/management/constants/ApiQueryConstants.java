package com.top.talent.management.constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiQueryConstants {
    public static final String QUERY_UID = "uid=in=(";
    public static final String QUERY_NAME = "native_full_name=fts=";
    public static final String QUERY_EMP_ID = "employment_id==";
    public static final String QUERY_BENCH_STATUS = "entity.person_id==";
    public static final String QUERY_CANDIDATE_ID = "candidate_id==";
    public static final String QUERY_PROFILE_EMPLOYEE = "profile_type.name==Employee";
    public static final String QUERY_EMAIL = "business_email==";
    public static final String QUERY_ABOVE_B3 = "((job_function_track==b and job_function_level>=3) or job_function_track==c)";

    public static final String QUERY_IS_ACTIVE = "entity.is_active==true";

    public static final String LOGICAL_AND = " and ";
    public static final String LOGICAL_OR = " or ";

    private ApiQueryConstants() {
    }
}

package com.top.talent.management.constants;

import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.dto.UserDTO;

import java.util.List;

public class Constants {

    public static final List<String> REQUEST_MATCHERS = List.of("/oauth2/**", "/login/oauth2/code/**", "/error", "/images/**");
    public static final List<String> SWAGGER_ENDPOINTS = List.of("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html");

    public static final String FILE_NAME = "STEP_\\d{4}_V\\d+\\.(xlsx|xls)$";

    public static final String CULTURAL_SCORE_FILE_NAME = "STEP_CULTURAL_SCORE_\\d{4}_V\\d+\\.(xlsx|xls)$";

    public static final String DRAFT_STATUS = "Draft submitted successfully!";
    public static final String SUBMIT_STATUS = "Ratings submitted successfully!";
    public static final String APPROVED_STATUS = "Ratings approved successfully!";


    public static final String USER_STATUS_ACTIVE = "Active";

    public static final String USER_STATUS_INACTIVE = "Inactive";

    public static final String USER_INACTIVE = "User has been made inactive.";

    public static final String USER_ADDED = "New user has been added successfully!";

    public static final String USER_DETAILS_UPDATED = "User details have been updated successfully!";

    public static final String USER_WELCOME_MAIL_SUBJECT = "Welcome to the STEP Program!";
    public static final String USER_WELCOME_MAIL_TEMPLATE = "WelcomeUser";

    public static final String HEROES_FILE_NAME = "STEP_HEROES_\\d{4}_V\\d+\\.(xlsx|xls)$";

    public static final String RISING_STAR="rising star";

    public static final String STAR="star";

    public static final String SUPER_STAR="super star";

    public static final List<String> REQUIRED_COLUMNS = List.of("NAME","UID", "Email", "Location","DOJ","Time with Epam","TITLE","STATUS","PRODUCTION CATEGORY","JOB FUNCTION"
            ,"RESOURCE MANAGER","PGM","PROJECT CODE","JF_LEVEL","Competency /Practice","Primary Skill","Niche Skills","Niche Skill(Yes/No)",
            "Talent Profile previous year","Talent Profile current year", "Delivery Feedback TT Score");

    public static final List<String> MANDATORY_COLUMNS = List.of(
            "NAME", "UID","Email", "DOJ", "TITLE", "RESOURCE MANAGER",
            "JF_LEVEL", "Competency /Practice", "Primary Skill",
            "Talent Profile current year", "Delivery Feedback TT Score"
    );

    public static final List<String> HEROES_REQUIRED_HEADERS = List.of("UID", "Rating Score for EngX", "Rating for Extra Mile");

    public static final List<String> CULTURE_SCORE_REQUIRED_HEADERS= List.of("UID","Culture Score (from feedback)");
    public static final String BEARER = "Bearer ";

    public static final String EMPTY_STRING="";

    public static final String SYSTEM = "System";

    public static final String DEFAULT_COMPETENCY = "All";

    public static final List<TopTalentEmployeeDTO> EMPTY_TOP_TALENT_LIST= List.of();

    public static final List<UserDTO> EMPTY_USER_LIST= List.of();

    public static final List<String> REQUIRED_JOB_LEVELS = List.of("B3", "B4", "B5", "C");

    public static final String STEP_2="STEP_2";

    public static final List<Integer> NO_OF_PLUS_DAYS_FOR_MONDAY= List.of(4,11,14,18);

    public static final List<Integer> NO_OF_PLUS_DAYS_FOR_NOT_MONDAY= List.of(6,13,14,20);

    public static final String CRON="0 0 17 * * *";

    public static final String IMAGE_URL= "src/main/resources/static/images/";

    public static final String EPAM_EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@epam\\.com$";

    public static final String IMAGE_PNG="image/png";

    public static final String STEP = "STEP";

    public static final String CULTURAL_SCORE = "CULTURAL_SCORE";

    public static final String HEROES = "HEROES";

    public static final String UNDERSCORE = "_";

    public  static final String STEP_SUFFIX = STEP + UNDERSCORE;

    public static final String HEROES_SUFFIX = HEROES + UNDERSCORE;

    public static final String CULTURAL_SCORE_SUFFIX = CULTURAL_SCORE + UNDERSCORE;

    public static final String VERSION_1 = "V1";

    public static final String VERSION_1_PREFIX = UNDERSCORE+VERSION_1;

    public static final String VERSION_PREFIX = UNDERSCORE+"V";

    public static final List<String> SUBJECTS = List.of( "Candidate List Uploaded for Top Talent Program Review",
            "Reminder: Candidate Rating Due on STEP Portal",
            "Second Reminder: Deadline Approaching for Candidate Rating on STEP Portal",
            "Third Reminder: Final Week to Complete Candidate Rating on STEP Portal",
            "Fourth and Final Reminder: Deadline Day for Candidate Rating on STEP Portal");


    public static final List<String> TEMPLATES = List.of("PracticeEmail1", "PracticeEmail2", "PracticeEmail3", "PracticeEmail4", "PracticeEmail5");
    public static final String SUBJECT_OF_FUTURE_SKILLS = "Future Skills Submission Notification";
    public static final String TEMPLATE_OF_FUTURE_SKILLS = "AdminEmailOnFutureSkills";

    public static final String FUTURE_SKILL_STATUS_NA = "Future Skills Cleared Successfully!";
    public static final String FUTURE_SKILL_STATUS_D = "Future Skills Saved As Draft Successfully!";
    public static final String FUTURE_SKILL_STATUS_S = "Future Skills Submitted Successfully!";
    public static final String FUTURE_SKILL_STATUS_A = "Future Skills Approved Successfully!";
    public static final String FUTURE_SKILL_EMAIL_SUBJECT = "Add Future Skills for Your Practice in the Top Talent Portal";
    public static final String FUTURE_SKILL_EMAIL_FILENAME = "FutureSkillEmail";

    public static final String NO_VERSION_NAME_NA = "NA";

    public static final String UNKNOWN_DATE_FORMAT = "Unknown date format";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String NOT_AVAIALABLE = "N/A";
    public static final String CLOSED = "Closed";


    private Constants() {
    }
}

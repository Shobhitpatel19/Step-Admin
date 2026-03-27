package com.top.talent.management.constants;

public class ErrorMessages {

    public static final String USER_NOT_FOUND_WITH_EMAIL = "User not found in the database with email: ";

    public static final String USER_NOT_FOUND_WITH_UID = "User not found in the database with UID: ";

    public static final String PRACTICE_NOT_NULL = "Practice cannot be null";

    public static final String ACCESS_DENIED = "You do not have permission to access this resource.";

    public static final String URI_ACCESS_DENIED = "Access Denied for user: {} on URI: {}";

    public static final String INVALID_JWT_SIGN = "Invalid JWT signature.";

    public static final String INVALID_JWT_TOKEN = "Invalid JWT token.";

    public static final String EXPIRED_JWT = "JWT token has expired.";

    public static final String UNSUPPORTED_JWT = "Unsupported JWT token.";

    public static final String CORRUPTED_EXCEL_FILE = "Error reading excel file, please make sure that the file is not corrupted and also a valid excel file";

    public static final String INVALID_FILE_FORMAT = "Invalid file name or format and Please make sure that the file name is in the correct format and also a valid excel file";

    public static final String EMPTY_FILE = "Uploaded Excel file is empty and Please make sure that the file is not empty";

    public static final String VERSION_ALREADY_EXISTS = "Version Already Exists";

    public static final String YEAR_MISMATCH = "The year in the filename does not match the current year";

    public static final String DUPLICATE_UID = "Duplicate UID detected: ";

    public static final String LIST_NOT_FOUND = "Cannot find eligibility list of this year OR version";

    public static final String NOT_CURRENT_YEAR = "Uploaded list is not of current year";

    public static final String CULTURE_SCORE_UID_MISMATCH = "UID mismatch: Cultural score for some UIDs is not available.";

    public static final String CULTURE_SCORE_SIZE_MISMATCH = "Size mismatch: Uploaded UIDs and Cultural Scores do not match.";

    public static final String INVALID_CANDIDATE = "Candidate is not a part of your Practice.";

    public static final String CANDIDATE_DOES_NOT_EXIST = "Candidate does not exist in Top Talent Employee list.";

    public static final String NULL_CULTURAL_SCORE = "Cultural score should not be empty or zero for UID: ";

    public static final String INVALID_CULTURAL_SCORE = "Cultural score should be between 0 to 4 for UID: ";

    public static final String MAIL_NOT_GENERATED = "Email is not generated for the mail id : ";

    public static final String MAIL_NOT_SENT = "Email is not sent to the mail id : ";

    public static final String USERS_NOT_SAVED = "Users are not saved";

    public static final String EMAIL_SCHEDULE_NOT_SAVED = "Email schedule dates are not saved";

    public static final String EXTRAMILE_INVALID_RATING_VALUE = "ExtraMile rating value should be between 0 and 4 for UID: ";

    public static final String ENGX_INVALID_RATING_VALUE = "EngX rating value should be between 0 and 4 for UID: ";

    public static final String INVALID_HEADER = "Invalid header, please make sure that the file has the correct headers";

    public static final String ENGX_EXTRA_MILE_SIZE_MISMATCH = "Size mismatch: Uploaded UIDs and Engx Extra Mile Rating do not match.";

    public static final String ENGX_EXTRA_MILE_UID_MISMATCH = "UID mismatch: Engx Extra Mile Rating for some UIDs is not available.";

    public static final String NULL_RATING_VALUE = "Rating value cannot be null for UID: ";

    public static final String NULL_UID = "UID cannot be null";

    public static final String MISSING_DOJ = "Date of joining (DOJ) is mandatory and can't have null values";


    public static final String MISSING_TITLE = "Title is mandatory and can't have null values";


    public static final String MISSING_RESOURCE_MANAGER = "Resource manager is mandatory and can't have null values";

    public static final String MISSING_JF_LEVEL = "JF level is mandatory and can't have null values";

    public static final String MISSING_COMPETENCY_PRACTICE = "Competency/practice is mandatory and can't have null values";

    public static final String MISSING_PRIMARY_SKILL = "Primary skill is mandatory and can't have null values";

    public static final String MISSING_TALENT_PROFILE = "Talent profile current year is mandatory and can't have null values";


    public static final String INVALID_UID = "UID must be exactly 6 numeric characters";

    public static final String PRACTICE_DELEGATION_IS_STEP_USER = "Your selected user is already a part of STEP program. So, you cannot delegate to this user";
    public static final String PRACTICE_DELEGATION_USER_NOT_FOUND = "Your selected user is not a valid EPAM employee";
    public static final String PRACTICE_DELEGATION_USER_NOT_ELIGIBLE = "You have to select user with minimum B3 job level to delegate";
    public static final String PRACTICE_DELEGATION_ALREADY_DELEGATED = "You have already delegated to another user, delete that first then retry";
    public static final String PRACTICE_DELEGATION_NO_FEATURE_SELECTED = "You have to select at least one feature to delegate for creating a delegate";
    public static final String PRACTICE_DELEGATION_INVALID_FEATURE_SELECTED = "You have selected one or more than one feature which is not available to delegate";
    public static final String PRACTICE_DELEGATION_NO_ACCESS_LEVEL_SELECTED = "You have to select is approval required or not for creating a delegate";
    public static final String PRACTICE_DELEGATION_NEVER_DELEGATED = "You have never created a delegate, create first then retry";
    public static final String PRACTICE_DELEGATION_DELEGATE_NOT_FOUND = "Delegate not found, You have not been delegated by any practice head";

    public static final String PRACTICE_RATING_NO_APPROVAL_PERMISSION = "You are trying to approve the rating but you don't have access to do approve";

    public static final String NO_DATA_FOUND= "API EPAM returned no data";


    public static final String UNABLE_TOKEN_FETCH="Unable to fetch token ";

    public static final String NO_RESPONSE_QUERY ="No data found for query: {}";

    public static final String NO_PHASES_FOUND = "No phases found";

    public static final String INVALID_FILE_NAME="Invalid file name: ";

    public static final String ERROR_SAVING_FILE = "Error in saving file, retry again.";
    public static final String PRACTICE_HEAD_NOT_FOUND = "Cannot find practice head for this competency";

    public static final String INVALID_DELIVERY_FEEDBACK_SCORE = "Delivery Feedback TT score should be between 0 to 4";

    public static final String NULL_DELIVERY_FEEDBACK_SCORE = "Delivery Feedback TT score should not be empty";

    public static final String ERROR_PROCESSING="Error processing request: {} ";

    public static final String VALIDATION_ERROR="Validation errors occurred";

    public static final String MESSAGE="message";

    public static final String INVALID_EMAIL="Invalid email id : {}";

    public static final String INVALID_RATING_APPROVAL = "Cannot approve ratings for this employee";

    public static final String INVALID_FORM_APPROVAL = "Cannot approve form for this employee";

    public static final String MISSING_NAME = "Name is mandatory and can't have null values";

    public static final String MISSING_EMAIL = "Email is mandatory and can't have null values";


    public static final String PRIORITY_MODIFICATION_NOT_ALLOWED="Changing the priority is not allowed.";

    public static final String ASPIRATION_LIMIT_EXCEEDED="You can only create up to 2 aspirations.";


    public static final String ASPIRATION_NOT_FOUND="Aspiration not found with priority: ";

    public static final String INCOMPLETE_ASPIRATIONS="Please ensure both Primary and Secondary aspirations are filled before submission.";

    public static final String ACKNOWLEDGMENT_BEFORE_CREATE_ASPIRATION = "Acknowledgment of FutureSkills guidelines is required before creating aspirations.";

    public static final String ACKNOWLEDGMENT_BEFORE_SUBMIT = "Acknowledgment is required before submitting aspirations.";

    public static final String CREATE_ASPIRATION1 = "Create priority aspiration1";

    public static final String CREATE_ASPIRATION2="Create priority aspiration2";

    public static final String ASPIRATIONS_SUBMITTED="Aspirations are already submitted";

    public static final String NOT_SEND_COMPETENCY_PH = "Do not send competency for practice head";

    public static final String COMPETENCY_NOT_SENT = "Admin cannot delegate without competency";

    public static final String NOTIFICATIONS_NOT_FOUND = "No notifications found for User with ID: ";
    public static final String FUTURE_SKILLS_NOT_FOUND="No latest future skills found" ;

    public static final String PREVIOUS_VERSION_FUTURE_SKILLS_NOT_FOUND="No previous future skills found";

    public static final String FUTURE_SKILLS_CATEGORY_NOT_FOUND="Category not found: ";

    public static final String INVALID_NOTIFICATION_REQUEST = "Invalid request: userId or categoryId is null,, or enable is null";
    public static final String PRACTICE_FORM_NO_APPROVAL_PERMISSION = "You are trying to approve the form submission but you don't have access to approve it";
    public static final String SELF_RATING_NOT_ALLOWED = "You cannot rate or approve your own rating";

    private ErrorMessages() {

    }
}
package com.top.talent.management.constants;

public class SwaggerConstants {

    public static final String BEARER_AUTH = "bearerAuth";
    public static final String MEDIA_TYPE_JSON = "application/json";
    public static final String ACCESS_DENIED = "Access Denied";
    public static final String BAD_REQUEST = "Bad Request";

    public static final String PRACTICE_RATING_GET_COMPETENCY_SUMMARY = "Get All Competencies";
    public static final String PRACTICE_RATING_GET_COMPETENCY_DESC_200 = "Successfully Retrieved Competencies";

    public static final String PRACTICE_RATING_GET_SUMMARY = "Get Ratings For Candidate";
    public static final String PRACTICE_RATING_GET_DESC_200 = "Successfully Retrieved Candidate Ratings";

    public static final String PRACTICE_RATING_POST_SUMMARY = "Post Ratings For Candidate";
    public static final String PRACTICE_RATING_POST_DESC_200 = "Successfully Saved Candidate Ratings";
    public static final String PRACTICE_RATING_POST_DESC_401 = "Sorry, You Cannot Rate This Candidate.";

    public static final String PRACTICE_RATING_LIST_GET_SUMMARY = "Get List Of Candidates";
    public static final String PRACTICE_RATING_LIST_GET_DESC_200 = "Successfully Retrieved Candidates";
    public static final String PRACTICE_RATING_POST_APPROVE_ALL_SUMMARY = "Post Ratings For Practice Candidate To Change All Submit Status To Approved";
    public static final String PRACTICE_RATING_POST_APPROVE_ALL_200 = "Successfully Approved All Candidate Ratings";

    public static final String PRACTICE_RATING_DESC_401 = "Sorry, You Cannot Rate This Candidate.";

    public static final String SUPER_ADMIN_GET_SUMMARY = "Get All Users By Role";
    public static final String SUPER_ADMIN_GET_DESC_200 = "Successfully Retrieved Users By Role";

    public static final String SUPER_ADMIN_POST_SUMMARY = "Add User by Role";
    public static final String SUPER_ADMIN_POST_DESC_200 = "User Successfully Added / Role Successfully Updated";

    public static final String SUPER_ADMIN_DELETE_SUMMARY = "Deactivate User";
    public static final String SUPER_ADMIN_DELETE_DESC_200 = "User Successfully Deactivated";

    public static final String ENGX_EXTRAMILE_POST= "Upload Engx ExtraMile Contributions";
    public static final String ENGX_EXTRAMILE_POST_DESC_200 = "Successfully Uploaded Engx ExtraMile Contributions";

    public static final String CULTURAL_SCORE_POST = "Upload Cultural Score";
    public static final String CULTURAL_SCORE_POST_DESC_200 = "Successfully Uploaded Cultural Score";

    public static final String TOP_TALENT_EMPLOYEE_UPLOAD = "Upload Top Talent Employee Excel";
    public static final String TOP_TALENT_EMPLOYEE_UPLOAD_DESC_200 = "Successfully Uploaded Top Talent Employee Excel";

    public static final String GET_EMPLOYEE_DATA = "Get Employee Data";
    public static final String GET_EMPLOYEE_DATA_DESC_200 = "Successfully Retrieved Employee Data";
    public static final String PRACTICE_DELEGATION_CREATE_DELEGATE_SUMMARY = "Delegate Responsibilities To Other User";
    public static final String PRACTICE_DELEGATION_CREATE_DELEGATE_SUMMARY_200 = "Delegation Created Successfully!";

    public static final String PRACTICE_DELEGATION_GET_DELEGATE_SUMMARY = "Get Delegate Details If Already Delegated";
    public static final String PRACTICE_DELEGATION_GET_DELEGATE_SUMMARY_200 = "Delegate Details Fetched Successfully!";

    public static final String PRACTICE_DELEGATION_DELETE_DELEGATE_SUMMARY = "Delete Already Created Delegate";
    public static final String PRACTICE_DELEGATION_DELETE_DELEGATE_SUMMARY_200 = "Delegate Deleted Successfully!";

    public static final String PRACTICE_DELEGATION_GET_AVAILABLE_FEATURE_SUMMARY = "Get List Of Available Practice Features Which Can Be Delegated";
    public static final String PRACTICE_DELEGATION_GET_AVAILABLE_FEATURE_SUMMARY_200 = "List Of Available Practice Features Fetched Successfully!";

    public static final String PRACTICE_DELEGATE_USER_GET_DELEGATED_FEATURE_SUMMARY = "Get List Of Delegated Practice Features Of A Delegated Using Delegate Email";
    public static final String PRACTICE_DELEGATE_USER_GET_DELEGATED_FEATURE_SUMMARY_200 = "List Of Delegated Practice Features Of A Delegate Fetched Successfully!";

    public static final String PRACTICE_DELEGATE_USER_IS_APPROVAL_REQUIRED_SUMMARY = "Get Is Approval Required For Submitting Data Into Any Delegated Feature By Delegate";
    public static final String PRACTICE_DELEGATE_USER_IS_APPROVAL_REQUIRED_SUMMARY_200 = "Boolean Value Of Is Approval Required Fetched Successfully!";

    public static final String PRACTICE_DELEGATE_USER_HAS_FEATURE_ACCESS_SUMMARY = "Get Has Delegate Access To A Particular Feature Or Not";
    public static final String PRACTICE_DELEGATE_USER_HAS_FEATURE_ACCESS_SUMMARY_200 = "Boolean Value Of Has Delegate Access To A Particular Feature Or Not Fetched Successfully!";

    public static final String GET_LIST_OF_CANDIDATE_ASPIRATIONS="get list of candidate aspirations";
    public static final String GET_LIST_OF_CANDIDATE_ASPIRATIONS_DESC_200 = "Successfully retrieved list of candidate aspirations";

    public static  final String CANDIDATE_ASPIRATION_POST = "save the candidate aspiration";
    public static final String CANDIDATE_ASPIRATION_POST_DESC_200 = "Successfully saved candidate aspiration";

    public static  final String CANDIDATE_ASPIRATION_DELETE = "delete the candidate aspiration";
    public static final String CANDIDATE_ASPIRATION_DELETE_DESC_200 = "Successfully deleted candidate aspiration";

    public static  final String CANDIDATE_ASPIRATION_PUT = "Update the candidate aspiration";
    public static final String CANDIDATE_ASPIRATION_PUT_DESC_200 = "Successfully updated candidate aspiration";

    public static  final String GET_CANDIDATE_ASPIRATION_TEMPLATE = "Get the candidate aspiration template";
    public static final String GET_CANDIDATE_ASPIRATION_TEMPLATE_DESC_200 = "Successfully retrieved candidate aspiration template";

    public static  final String GET_CANDIDATE_ASPIRATION_BY_PRIORITY = "Get the candidate aspiration by priority";
    public static final String GET_CANDIDATE_ASPIRATION_BY_PRIORITY_DESC_200 = "Successfully retrieved candidate aspiration by priority";

    public static  final String SUBMIT_CANDIDATE_ASPIRATION = "submit the candidate aspiration";
    public static final String SUBMIT_CANDIDATE_ASPIRATION_DESC_200 = "Successfully submitted candidate aspiration";

    public static final String GET_EMPLOYEE_PROFILE_BY_EMAIL = "Get employee profile by email";
    public static final String GET_EMPLOYEE_PROFILE_BY_EMAIL_DESC_200 = "Successfully retrieved employee profile ";

    public static final String GET_USER_WITH_FEATURES = "Get all users with their notification settings and features";
    public static final String TOGGLE_NOTIFICATION = "Enable or disable notifications for a specific user and category";
    public static final String TOGGLE_ALL_NOTIFICATIONS = "Enable or disable all notifications for a specific user";

    public static final String GET_USER_WITH_FEATURES_200_DESC = "Successfully retrieved all users with their notification features.";
    public static final String TOGGLE_NOTIFICATION_200_DESC = "Successfully toggled the notification for the specified user and category.";
    public static final String TOGGLE_ALL_NOTIFICATIONS_200_DESC = "Successfully toggled all notifications for the specified user.";


    private SwaggerConstants() {
    }
}

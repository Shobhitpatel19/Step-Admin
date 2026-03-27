package com.top.talent.management.constants;

public class RoleConstants {

    private static final String ROLE_FORMAT = "ROLE_";
    private static final String HAS_ROLE_FORMAT = "hasRole('";
    private static final String HAS_ANY_ROLE_FORMAT = "hasAnyRole(";

    public static final String PRACTICE = "P";
    public static final String SUPER_ADMIN = "SA";
    public static final String SUPER_USER = "SU";
    public static final String USER = "U";
    public static final String HRBP = "HRBP";

    public static final String ROLE_PRACTICE = ROLE_FORMAT + PRACTICE;
    public static final String ROLE_SUPER_ADMIN = ROLE_FORMAT + SUPER_ADMIN;
    public static final String ROLE_SUPER_USER = ROLE_FORMAT + SUPER_USER;
    public static final String ROLE_USER = ROLE_FORMAT + USER;

    public static final String ROLE_SU_SA = "'"+ROLE_SUPER_USER+"','"+ROLE_SUPER_ADMIN+"'";
    public static final String ROLE_SA_P = "'"+ROLE_SUPER_ADMIN+"','"+ROLE_PRACTICE+"'";
    public static final String ROLE_SU_SA_P = "'"+ROLE_SUPER_USER+"','"+ROLE_SUPER_ADMIN+"','"+ROLE_PRACTICE+"'";
    public static final String ROLE_SU_SA_P_U = "'"+ROLE_SUPER_USER+"','"+ROLE_SUPER_ADMIN+"','"+ROLE_PRACTICE+"','"+ROLE_USER+"'";

    public static final String HAS_ROLE_SUPER_USER = HAS_ROLE_FORMAT+ROLE_SUPER_USER+"')";
    public static final String HAS_ROLE_SUPER_ADMIN = HAS_ROLE_FORMAT+ROLE_SUPER_ADMIN+"')";
    public static final String HAS_ROLE_PRACTICE = HAS_ROLE_FORMAT+ROLE_PRACTICE+"')";
    public static final String HAS_ROLE_USER = HAS_ROLE_FORMAT+ROLE_USER+"')";

    public static final String HAS_ANY_ROLE_SU_SA = HAS_ANY_ROLE_FORMAT + ROLE_SU_SA + ")";
    public static final String HAS_ANY_ROLE_SA_P = HAS_ANY_ROLE_FORMAT + ROLE_SA_P + ")";
    public static final String HAS_ANY_ROLE_SU_SA_P = HAS_ANY_ROLE_FORMAT + ROLE_SU_SA_P + ")";
    public static final String HAS_ANY_ROLE_SU_SA_P_U = HAS_ANY_ROLE_FORMAT + ROLE_SU_SA_P_U + ")";

    private RoleConstants() {}
}

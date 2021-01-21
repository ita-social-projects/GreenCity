package greencity.constant;

public class RestTemplateLinks {
    public static final String EMAIL = "?email=";
    public static final String USER_FIND_BY_EMAIL = "/user/findByEmail";
    public static final String ID = "?id=";
    public static final String USER_FIND_BY_ID = "/user/findById";
    public static final String USER_FIND_BY_ID_FOR_ACHIEVEMENT = "/user/findByIdForAchievement";
    public static final String USER_FIND_USER_FOR_MANAGEMENT = "/user/findUserForManagement";
    public static final String PAGE = "?page=";
    public static final String SIZE = "&size=";
    public static final String QUERY = "&query=";
    public static final String SEARCH_BY = "/user/searchBy";
    public static final String USER = "/user";
    public static final String USER_FIND_ALL = "user/findAll";
    public static final String FRIENDS = "/friends";
    public static final String USER_FIND_NOT_DEACTIVATED_BY_EMAIL = "user/findNotDeactivatedByEmail";
    public static final String USER_FIND_ID_BY_EMAIL = "user/findIdByEmail";
    public static final String UPDATE_USER_LAST_ACTIVITY_TIME = "/updateUserLastActivityTime/";
    public static final String USER_DEACTIVATE = "user/deactivate";
    public static final String USER_ACTIVATE = "user/activate";
    public static final String OWN_SECURITY_REGISTER = "ownSecurity/register";

    private RestTemplateLinks() {
    }
}

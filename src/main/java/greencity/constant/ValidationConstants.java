package greencity.constant;

public class ValidationConstants {
    public static final String EMPTY_FIRSTNAME = "The firstName field can not be empty";
    public static final String INVALID_FIRSTNAME_LENGTH = "The firstName field should be between 2 and 20 characters";
    public static final int FIRSTNAME_MIN_LENGTH = 2;
    public static final int FIRSTNAME_MAX_LENGTH = 20;

    public static final String EMPTY_LASTNAME = "The lastName field can not be empty";
    public static final String INVALID_LASTNAME_LENGTH = "The lastName field should be between 2 and 20 characters";
    public static final int LASTNAME_MIN_LENGTH = 2;
    public static final int LASTNAME_MAX_LENGTH = 20;

    public static final String EMPTY_EMAIL = "The email field can not be empty";
    public static final String INVALID_EMAIL = "The email is invalid";

    public static final String EMPTY_ID = "The id field can not be empty";
    public static final String EMPTY_STATUS = "The status field can not be empty";

    public static final int PLACE_NAME_MAX_LENGTH = 30;
    public static final int PLACE_ADDRESS_MAX_LENGTH = 60;
    public static final int PLACE_ADDRESS_MIN_LENGTH = 3;

    public static final String CATEGORY_NAME_BAD_FORMED = "Bad formed category name: ${validatedValue}";
    public static final int CATEGORY_NAME_MAX_LENGTH = 30;
    public static final int CATEGORY_NAME_MIN_LENGTH = 3;

    public static final String EMPTY_PLACE_NAME = "The name of place field can not be empty";
    public static final String EMPTY_PLACE_ADDRESS = "The address of place field can not be empty";

    public static final String EMPTY_NAME_OF_CATEGORY = "The category name field can not be empty";

    public static final String EMPTY_VALUE_OF_LATITUDE = "The latitude can not be empty";
    public static final String EMPTY_VALUE_OF_LONGITUDE = "The longitude can not be empty";

    public static final String EMPTY_OPEN_TIME_VALUE = "The opening time can not be empty";
    public static final String EMPTY_CLOSE_TIME_VALUE = "The closing time can not be empty";

    public static final String EMPTY_WEEK_DAY_VALUE = "The week day can not be empty";
}

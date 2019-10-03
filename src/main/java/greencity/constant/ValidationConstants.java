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
    public static final String INVALID_PASSWORD = "Password has contain at least one character of "
        + "Uppercase letter (A-Z), "
        + "Lowercase letter (a-z), "
        + "Digit (0-9), "
        + "Special character (~`!@#$%^&*()+=_-{}[]|:;”’?/<>,.).";

    public static final String EMPTY_ID = "The id field can not be empty";
    public static final String NEGATIVE_ID = "The id should be positive number";
    public static final String EMPTY_STATUS = "The status field can not be empty";

    public static final int PLACE_NAME_MAX_LENGTH = 30;
    public static final int PLACE_ADDRESS_MAX_LENGTH = 120;
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

    public static final String MIN_VALUE_LATITUDE =
        "The '${validatedValue}' must be at least {value}";
    public static final String MAX_VALUE_LATITUDE =
        "The '${validatedValue}' must be at least {value}";
    public static final String MIN_VALUE_LONGITUDE =
        "The '${validatedValue}' must be at least {value}";
    public static final String MAX_VALUE_LONGITUDE =
        "The '${validatedValue}' must be at least {value}";

    public static final String N_E_LAT_CAN_NOT_BE_NULL = "North-east latitude can not be null";
    public static final String N_E_LNG_CAN_NOT_BE_NULL = "North-east longitude can not be null";
    public static final String S_W_LAT_CAN_NOT_BE_NULL = "South-west latitude can not be null";
    public static final String S_W_LNG_CAN_NOT_BE_NULL = "South-west longitude can not be null";

    public static final String LAT_MIN_VALIDATION = "Has to be greatest or equals -90";
    public static final String LAT_MAX_VALIDATION = "Has to be lover or equals 90";
    public static final String LNG_MIN_VALIDATION = "Has to be greatest or equals -180";
    public static final String LNG_MAX_VALIDATION = "Has to be lover or equals 180";

    public static final int DISCOUNT_VALUE_MIN = 0;
    public static final String DISCOUNT_VALUE_DOES_NOT_CORRECT = "Min discount value is 0, max discount value is 100";
    public static final int DISCOUNT_VALUE_MAX = 100;
    public static final String EMPTY_SPECIFICATION_NAME = "The specification name can not be empty";
}

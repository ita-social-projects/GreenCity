package greencity.constant;

public final class ValidationConstants {
    public static final int PLACE_NAME_MAX_LENGTH = 30;

    public static final String CATEGORY_NAME_BAD_FORMED = "Bad formed category name: ${validatedValue}";
    public static final int CATEGORY_NAME_MAX_LENGTH = 30;
    public static final int CATEGORY_NAME_MIN_LENGTH = 3;

    public static final String EMPTY_PLACE_NAME = "The name of place field can not be empty";

    public static final String EMPTY_NAME_OF_CATEGORY = "The category name field can not be empty";

    private ValidationConstants() {
    }
}

package greencity.constant;

public final class ServiceValidationConstants {
    public static final int ADVICE_MIN_LENGTH = 3;
    public static final int ADVICE_MAX_LENGTH = 300;
    public static final int MIN_AMOUNT_OF_ITEMS = 0;
    public static final int MAX_AMOUNT_OF_ITEMS = 16;
    public static final String CATEGORY_NAME_BAD_FORMED = "{greenCity.validation.bad.formed.category.name}";
    public static final int CATEGORY_NAME_MIN_LENGTH = 3;
    public static final int CATEGORY_NAME_MAX_LENGTH = 30;
    public static final int PLACE_NAME_MAX_LENGTH = 30;
    public static final int MAX_AMOUNT_OF_TAGS = 3;
    public static final int HABIT_FACT_MIN_LENGTH = 3;
    public static final int HABIT_FACT_MAX_LENGTH = 300;
    public static final int USERNAME_MIN_LENGTH = 6;
    public static final int USERNAME_MAX_LENGTH = 30;
    public static final String INVALID_EMAIL = "{greenCity.validation.invalid.email}";
    public static final String MIN_AMOUNT_OF_TAGS = "{greenCity.validation.empty.tags}";
    public static final int MAX_AMOUNT_OF_SOCIAL_NETWORK_LINKS = 5;
    public static final int PLACE_ADDRESS_MAX_LENGTH = 120;
    public static final int PLACE_ADDRESS_MIN_LENGTH = 3;
    public static final String EMPTY_VALUE_OF_LATITUDE = "{greenCity.validation.empty.latitude}";
    public static final String EMPTY_VALUE_OF_LONGITUDE = "{greenCity.validation.empty.longitude}";
    public static final int DISCOUNT_VALUE_MIN = 0;
    public static final String DISCOUNT_VALUE_DOES_NOT_CORRECT = "{greenCity.validation.invalid.discount.value}";
    public static final int DISCOUNT_VALUE_MAX = 100;

    private ServiceValidationConstants() {
    }
}

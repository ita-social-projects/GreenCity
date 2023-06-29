package greencity;

import java.time.LocalDateTime;

public final class TestConst {
    public static final String SITE = "https://google.com/";
    public static final String NAME = "Taras";
    public static final String EMAIL = "taras@gmail.com";
    public static final String IMG_NAME = "Screenshot.png";
    public static final String TOKEN = "SuperSecretAccessToken";
    public static final String EXCEPTION_MESSAGE = "Example failure";
    public static final String MESSAGE_JSON = "{ \"message\":\"" + EXCEPTION_MESSAGE + "\" }";
    public static final String INCORRECT_MESSAGE_JSON = "Not a json";
    public static final String REQUEST_URI = "api/example";
    public static final String TRACE = "example trace";
    public static final String TIME_STAMP = LocalDateTime.of(2022, 12, 23, 13, 52).toString();
}

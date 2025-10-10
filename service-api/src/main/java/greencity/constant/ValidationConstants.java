package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {
    public static final int PLACE_NAME_MAX_LENGTH = 30;
    public static final String USERNAME_REGEXP =
        "^[ґҐіІєЄїЇА-Яа-яa-zA-Z](?!.*\\.$)(?!.*?\\.\\.)(?!.*?--)(?!.*?'')[-'ʼ’ ґҐіІєЄїЇА-Яа-я\\w.]{0,29}$";
    public static final String EMAIL_REGEXP =
        "^(?=.{3,72}$)"
            + "([a-zA-Z0-9!#$%&'*+/=?^_{|}~-]+"
            + "(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_{|}~-]+)*)"
            + "@"
            + "(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+"
            + "[a-zA-Z]{2,63}|"
            + "\\[(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)"
            + "(?:\\.(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)){3}\\])$";
    public static final String INVALID_EMAIL = "{greenCity.validation.invalid.email}";
    public static final String USERNAME_MESSAGE = """
        Name must start with a letter, \
        cannot end with dot \
        or contain 2 consecutive dots, dashes and special symbols. \
        Use English or Ukrainian letters, \
        no longer than 30 symbols.\
        """;
}

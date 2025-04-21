package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {
    public static final int PLACE_NAME_MAX_LENGTH = 30;
    public static final String USERNAME_REGEXP =
        "^[ґҐіІєЄїЇА-Яа-яa-zA-Z](?!.*\\.$)(?!.*?\\.\\.)(?!.*?--)(?!.*?'')[-'ʼ’ ґҐіІєЄїЇА-Яа-я\\w.]{0,29}$";
}

package greencity.exception.exceptions;

import static greencity.constant.UserEcoNewsRelevanceConstants.USER_NOT_FOUND_MESSAGE;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super(USER_NOT_FOUND_MESSAGE + userId);
    }
}

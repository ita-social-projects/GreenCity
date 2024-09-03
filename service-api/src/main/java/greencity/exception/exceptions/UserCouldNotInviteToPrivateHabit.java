package greencity.exception.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class UserCouldNotInviteToPrivateHabit extends RuntimeException {
    public UserCouldNotInviteToPrivateHabit(String message) {
        super(message);
    }
}

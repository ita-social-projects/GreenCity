package greencity.message;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * The serializable message that is used for password recovery messages sending.
 */
@Getter
@ToString
public final class PasswordRecoveryMessage implements Serializable {
    private final Long userId;
    private final String userFirstName;
    private final String userEmail;
    private final String recoveryToken;
    private final String language;

    /**
     * Constructor with all required for email sending dependencies declared.
     *
     * @param userId        the user id is used for recovery link building.
     * @param userFirstName user first name is used in email model constants.
     * @param userEmail     user email which will be used for sending recovery
     *                      letter.
     * @param recoveryToken password recovery token.
     * @param language      language which will be used for sending recovery letter.
     */
    public PasswordRecoveryMessage(Long userId, String userFirstName, String userEmail, String recoveryToken,
        String language) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userEmail = userEmail;
        this.recoveryToken = recoveryToken;
        this.language = language;
    }
}

package greencity.message;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Message, that is used for sending emails for approving user registration.
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class UserApprovalMessage implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String token;
}

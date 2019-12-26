package greencity.service;

import greencity.entity.User;
import java.security.Principal;

public interface UserValidationService {
    /**
     * Method checks if current user is allowed to perform actions.
     *
     * @param principal {@link Principal} - current user.
     * @param id - id from resource path.
     */
    User userValidForActions(Principal principal, Long id);
}

package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.User;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadEmailException;
import greencity.exception.NotCurrentUserException;
import greencity.exception.UserBlockedException;
import greencity.service.UserService;
import greencity.service.UserValidationService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserValidationServiceImpl implements UserValidationService {
    private final UserService userService;

    /**
     * Constructor.
     */
    @Autowired
    public UserValidationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User userValidForActions(Principal principal, Long id) {
        User user = userService.findByEmail(principal.getName())
            .orElseThrow(() -> new BadEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + principal.getName()));
        if (user.getUserStatus().equals(UserStatus.BLOCKED)) {
            throw new UserBlockedException(ErrorMessage.USER_HAS_BLOCKED_STATUS);
        }
        if (!user.getId().equals(id)) {
            throw new NotCurrentUserException(ErrorMessage.NOT_A_CURRENT_USER);
        }
        return user;
    }
}

package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.exceptions.BadEmailException;
import greencity.exception.exceptions.NotCurrentUserException;
import greencity.exception.exceptions.UserBlockedException;
import greencity.service.UserService;
import greencity.service.UserValidationService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UserValidationServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private Principal principal;

    private UserValidationService userValidationService;

    private User testValidUser =
        User.builder()
            .id(1L)
            .firstName("test")
            .lastName("test")
            .email("test@gmail.com")
            .role(ROLE.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();

    private User testBlockedUser =
        User.builder()
            .email("test@gmail.com")
            .userStatus(UserStatus.BLOCKED)
            .build();

    @Before
    public void setup() {
        userValidationService = new UserValidationServiceImpl(userService);
    }

    @Test(expected = BadEmailException.class)
    public void userValidForActionsUserDoesNotExists() {
        when(userService.findByEmail(any()))
            .thenThrow(BadEmailException.class);
        userValidationService.userValidForActions(principal, 1L);
    }

    @Test(expected = UserBlockedException.class)
    public void userValidForActionsUserBlocked() {
        when(userService.findByEmail(any())).thenReturn(Optional.ofNullable(testBlockedUser));
        userValidationService.userValidForActions(principal, 1L);
    }

    @Test(expected = NotCurrentUserException.class)
    public void userValidForActionsUserIsNotCurrent() {
        when(userService.findByEmail(any())).thenReturn(Optional.ofNullable(testValidUser));
        userValidationService.userValidForActions(principal, 2L);
    }

    @Test
    public void userValidForActions() {
        when(userService.findByEmail(any())).thenReturn(Optional.ofNullable(testValidUser));
        User user = userValidationService.userValidForActions(principal, 1L);
        assertEquals(testValidUser, user);
    }
}

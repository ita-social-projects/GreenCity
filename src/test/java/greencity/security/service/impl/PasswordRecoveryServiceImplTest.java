package greencity.security.service.impl;

import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.exception.exceptions.BadEmailException;
import greencity.exception.exceptions.BadVerifyEmailTokenException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.message.PasswordRecoveryMessage;
import greencity.repository.UserRepo;
import greencity.security.events.SendRestorePasswordEmailEvent;
import greencity.security.events.UpdatePasswordEvent;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.RestorePasswordEmailRepo;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasswordRecoveryServiceImplTest {
    @Mock
    private JwtTool jwtTool;
    @Mock
    private RestorePasswordEmailRepo restorePasswordEmailRepo;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private UserRepo userRepo;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private PasswordRecoveryServiceImpl passwordRecoveryService;
    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;
    private static final String PASSWORD_RECOVERY_PATTERN = ".password.recovery";

    @Test(expected = NotFoundException.class)
    public void sendPasswordRecoveryEmailToNonExistentUserTest() {
        String email = "foo";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
        passwordRecoveryService.sendPasswordRecoveryEmailTo(email);
    }

    @Test(expected = BadEmailException.class)
    public void sendPasswordRecoveryEmailToUserWithExistentRestorePasswordEmailTest() {
        String email = "foo";
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(
            User.builder().restorePasswordEmail(new RestorePasswordEmail()).build()
        ));
        passwordRecoveryService.sendPasswordRecoveryEmailTo(email);
    }

    @Test
    public void sendPasswordRecoveryEmailToSimpleTest() {
        String email = "foo";
        User user = new User();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        String token = "bar";
        when(jwtTool.generateTokenKey()).thenReturn(token);
        ReflectionTestUtils.setField(passwordRecoveryService, "tokenExpirationTimeInHours", 24);
        passwordRecoveryService.sendPasswordRecoveryEmailTo(email);
        verify(restorePasswordEmailRepo).save(refEq(
            RestorePasswordEmail.builder()
                .user(user)
                .token(token)
                .build(), "expiryDate"
        ));
        verify(rabbitTemplate).convertAndSend(
            refEq(sendEmailTopic),
            refEq(sendEmailTopic + PASSWORD_RECOVERY_PATTERN),
            refEq(new PasswordRecoveryMessage(
                user.getId(),
                user.getFirstName(),
                user.getEmail(),
                token
            ))
        );
    }

    @Test(expected = BadVerifyEmailTokenException.class)
    public void updatePasswordUsingTokenWithNonExistentTokenTest() {
        String token = "foo";
        String newPassword = "bar";
        when(restorePasswordEmailRepo.findByToken(token)).thenReturn(Optional.empty());
        passwordRecoveryService.updatePasswordUsingToken(token, newPassword);
    }

    @Test(expected = UserActivationEmailTokenExpiredException.class)
    public void updatePasswordUsingTokenWithExpiredTokenTest() {
        String token = "foo";
        String newPassword = "bar";
        when(restorePasswordEmailRepo.findByToken(token)).thenReturn(Optional.of(
            RestorePasswordEmail.builder()
                .expiryDate(LocalDateTime.now().minusHours(1))
                .user(User.builder().email("foo@bar.com").build())
                .build()
        ));
        passwordRecoveryService.updatePasswordUsingToken(token, newPassword);
    }

    @Test
    public void updatePasswordUsingTokenSimpleTest() {
        String token = "foo";
        String newPassword = "bar";
        User user = User.builder().id(1L).email("foo@bar.com").build();
        when(restorePasswordEmailRepo.findByToken(token)).thenReturn(Optional.of(
            RestorePasswordEmail.builder()
                .expiryDate(LocalDateTime.now().plusHours(1))
                .user(user)
                .token(token)
                .build()
        ));
        passwordRecoveryService.updatePasswordUsingToken(token, newPassword);
        verify(restorePasswordEmailRepo).delete(refEq(
            RestorePasswordEmail.builder()
                .user(user)
                .token(token)
                .build(), "expiryDate"
        ));
        verify(applicationEventPublisher).publishEvent(refEq(
            new UpdatePasswordEvent(passwordRecoveryService.getClass(), newPassword, user.getId()), "timestamp"
        ));
    }
}

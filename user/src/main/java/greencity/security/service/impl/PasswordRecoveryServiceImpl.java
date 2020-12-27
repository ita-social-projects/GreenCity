package greencity.security.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadVerifyEmailTokenException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.message.PasswordRecoveryMessage;
import greencity.repository.UserRepo;
import greencity.security.events.UpdatePasswordEvent;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.PasswordRecoveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static greencity.constant.ErrorMessage.EMAIL_TOKEN_EXPIRED;
import static greencity.constant.ErrorMessage.NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN;
import static greencity.constant.RabbitConstants.PASSWORD_RECOVERY_ROUTING_KEY;

/**
 * Service for password recovery functionality. It manages recovery tokens
 * creation and persistence as well as minimal validation, but neither updates
 * the password directly, nor sends a recovery email. These parts of the
 * recovery process are done by separate event listeners.
 */
@Service
@Slf4j
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {
    private final UserRepo userRepo;
    private final RestorePasswordEmailRepo restorePasswordEmailRepo;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RabbitTemplate rabbitTemplate;
    private final JwtTool jwtTool;
    @Value("${verifyEmailTimeHour}")
    private Integer tokenExpirationTimeInHours;
    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;

    /**
     * Constructor with all essentials beans for password recovery functionality.
     *
     * @param restorePasswordEmailRepo  {@link RestorePasswordEmailRepo} - Used for
     *                                  storing recovery tokens
     * @param applicationEventPublisher {@link ApplicationEventPublisher} - Used for
     *                                  publishing events, such as email sending or
     *                                  password update
     * @param rabbitTemplate            template for sending RabbitMQ messages
     * @param jwtTool                   {@link JwtTool} - Used for recovery token
     *                                  generation
     */
    public PasswordRecoveryServiceImpl(
        RestorePasswordEmailRepo restorePasswordEmailRepo,
        UserRepo userRepo,
        ApplicationEventPublisher applicationEventPublisher,
        RabbitTemplate rabbitTemplate,
        JwtTool jwtTool) {
        this.restorePasswordEmailRepo = restorePasswordEmailRepo;
        this.userRepo = userRepo;
        this.applicationEventPublisher = applicationEventPublisher;
        this.rabbitTemplate = rabbitTemplate;
        this.jwtTool = jwtTool;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void sendPasswordRecoveryEmailTo(String email, String language) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        RestorePasswordEmail restorePasswordEmail = user.getRestorePasswordEmail();
        if (restorePasswordEmail != null) {
            throw new WrongEmailException(ErrorMessage.PASSWORD_RESTORE_LINK_ALREADY_SENT + email);
        }
        savePasswordRestorationTokenForUser(user, jwtTool.generateTokenKey(), language);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void updatePasswordUsingToken(String token, String newPassword) {
        RestorePasswordEmail restorePasswordEmail = restorePasswordEmailRepo
            .findByToken(token)
            .orElseThrow(() -> new BadVerifyEmailTokenException(NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN));
        UserStatus userStatus = restorePasswordEmail.getUser().getUserStatus();
        if (isNotExpired(restorePasswordEmail.getExpiryDate())) {
            applicationEventPublisher.publishEvent(
                new UpdatePasswordEvent(this, newPassword, restorePasswordEmail.getUser().getId()));
            restorePasswordEmailRepo.delete(restorePasswordEmail);
            log.info("User with email " + restorePasswordEmail.getUser().getEmail()
                + " has successfully restored the password using token " + token);
        } else {
            log.info("Password restoration token of user with email " + restorePasswordEmail.getUser().getEmail()
                + " has been expired. Token: " + token);
            throw new UserActivationEmailTokenExpiredException(EMAIL_TOKEN_EXPIRED);
        }
        if (userStatus == UserStatus.CREATED) {
            restorePasswordEmail.getUser().setUserStatus(UserStatus.ACTIVATED);
        }
    }

    /**
     * Creates and saves password restoration token for a given user and publishes
     * event of sending password recovery email to the user.
     *
     * @param user  {@link User} - User whose password is to be recovered
     * @param token {@link String} - token for password restoration
     */
    private void savePasswordRestorationTokenForUser(User user, String token, String language) {
        RestorePasswordEmail restorePasswordEmail =
            RestorePasswordEmail.builder()
                .user(user)
                .token(token)
                .expiryDate(calculateExpirationDate(tokenExpirationTimeInHours))
                .build();
        restorePasswordEmailRepo.save(restorePasswordEmail);
        rabbitTemplate.convertAndSend(
            sendEmailTopic,
            PASSWORD_RECOVERY_ROUTING_KEY,
            new PasswordRecoveryMessage(
                user.getId(),
                user.getFirstName(),
                user.getEmail(),
                token,
                language));
    }

    /**
     * Checks if the given date is before current {@link LocalDateTime}. Use this
     * method for checking for token expiration.
     *
     * @param tokenExpirationDate - Token expiration date
     * @return {@code boolean} - Whether token is expired or not
     */
    private boolean isNotExpired(LocalDateTime tokenExpirationDate) {
        return LocalDateTime.now().isBefore(tokenExpirationDate);
    }

    /**
     * Calculates token expiration date. The amount of hours, after which token will
     * be expired, is set by method parameter.
     *
     * @param expirationTimeInHours - Token expiration delay in hours
     * @return {@link LocalDateTime} - Time at which token will be expired
     */
    private LocalDateTime calculateExpirationDate(Integer expirationTimeInHours) {
        LocalDateTime now = LocalDateTime.now();
        return now.plusHours(expirationTimeInHours);
    }

    /**
     * Removes all the expired tokens from the database each period of time.
     * Interval is set by @Scheduled annotation. Access modifier is set to
     * package-private since this method should be invoked by Spring Framework only.
     */
    // every 86400000 milliseconds == every 24 hours
    @Scheduled(fixedRate = 86400000)
    void deleteAllExpiredPasswordResetTokens() {
        int rows = restorePasswordEmailRepo.deleteAllExpiredPasswordResetTokens();
        log.info(rows + " password reset tokens were deleted.");
    }
}

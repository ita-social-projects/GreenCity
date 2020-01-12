package greencity.security.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.exception.exceptions.BadEmailException;
import greencity.exception.exceptions.BadVerifyEmailTokenException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.repository.UserRepo;
import greencity.security.events.SendRestorePasswordEmailEvent;
import greencity.security.events.UpdatePasswordEvent;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.RestoreLogicService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static greencity.constant.ErrorMessage.EMAIL_TOKEN_EXPIRED;
import static greencity.constant.ErrorMessage.NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN;
import static greencity.constant.ErrorMessage.PASSWORD_RESTORE_LINK_ALREADY_SENT;

@Service
@Slf4j
public class RestoreLogicServiceImpl implements RestoreLogicService {
    private final UserRepo userRepo;
    private final RestorePasswordEmailRepo restorePasswordEmailRepo;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JwtTool jwtTool;
    @Value("${verifyEmailTimeHour}")
    private Integer tokenExpirationTimeInHours;

    /**
     * Constructor for RestoreLogicServiceImpl class.
     *
     * @param restorePasswordEmailRepo  {@link RestorePasswordEmailRepo}
     * @param applicationEventPublisher {@link ApplicationEventPublisher}
     * @param jwtTool                   {@link JwtTool}
     */
    public RestoreLogicServiceImpl(
        RestorePasswordEmailRepo restorePasswordEmailRepo,
        UserRepo userRepo,
        ApplicationEventPublisher applicationEventPublisher,
        JwtTool jwtTool
    ) {
        this.restorePasswordEmailRepo = restorePasswordEmailRepo;
        this.userRepo = userRepo;
        this.applicationEventPublisher = applicationEventPublisher;
        this.jwtTool = jwtTool;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void sendEmailForRestore(String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        RestorePasswordEmail restorePasswordEmail = user.getRestorePasswordEmail();
        if (restorePasswordEmail != null) {
            throw new BadEmailException(PASSWORD_RESTORE_LINK_ALREADY_SENT + email);
        }
        savePasswordRestorationTokenForUser(user, jwtTool.generateTokenKey());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void restoreByToken(String token, String password) {
        RestorePasswordEmail restorePasswordEmail = restorePasswordEmailRepo
            .findByToken(token)
            .orElseThrow(() -> new BadVerifyEmailTokenException(NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN));
        if (isNotExpired(restorePasswordEmail.getExpiryDate())) {
            applicationEventPublisher.publishEvent(
                new UpdatePasswordEvent(this, password, restorePasswordEmail.getUser().getId())
            );
            restorePasswordEmailRepo.delete(restorePasswordEmail);
            log.info("User has successfully restore the password.");
        } else {
            log.info("Password restoration token has expired.");
            throw new UserActivationEmailTokenExpiredException(EMAIL_TOKEN_EXPIRED);
        }
    }

    /**
     * Saves password restoration token for a given user.
     *
     * @param user  {@link User}
     * @param token {@link String} - token for password restoration.
     */
    private void savePasswordRestorationTokenForUser(User user, String token) {
        RestorePasswordEmail restorePasswordEmail =
            RestorePasswordEmail.builder()
                .user(user)
                .token(token)
                .expiryDate(calculateExpirationDate(tokenExpirationTimeInHours))
                .build();
        restorePasswordEmailRepo.save(restorePasswordEmail);
        applicationEventPublisher.publishEvent(
            new SendRestorePasswordEmailEvent(user, token)
        );
    }

    /**
     * Checks if the give date happened before.
     *
     * @param tokenExpirationDate - when a token expires.
     * @return {@code boolean}
     */
    private boolean isNotExpired(LocalDateTime tokenExpirationDate) {
        return LocalDateTime.now().isBefore(tokenExpirationDate);
    }

    private LocalDateTime calculateExpirationDate(Integer expirationTimeInHour) {
        LocalDateTime now = LocalDateTime.now();
        return now.plusHours(expirationTimeInHour);
    }

    /**
     * Deletes from the database password reset tokens that are expired.
     */
    @Scheduled(fixedRate = 86400000)
    void deleteAllExpiredPasswordResetTokens() {
        int rows = restorePasswordEmailRepo.deleteAllExpiredPasswordResetTokens();
        log.info(rows + " password reset tokens were deleted.");
    }
}

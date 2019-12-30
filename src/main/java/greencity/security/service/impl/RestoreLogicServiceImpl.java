package greencity.security.service.impl;

import static greencity.constant.ErrorMessage.*;

import greencity.constant.ErrorMessage;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.exception.exceptions.BadEmailException;
import greencity.exception.exceptions.BadVerifyEmailTokenException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.repository.UserRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.RestoreLogicService;
import greencity.security.service.RestorePasswordEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class RestoreLogicServiceImpl implements RestoreLogicService {
    private final RestorePasswordEmailService restorePasswordEmailService;
    private final UserRepo userRepo;
    private final RestorePasswordEmailRepo repo;
    private final OwnSecurityService ownSecurityService;

    /**
     * Constructor for RestoreLogicServiceImpl class.
     *
     * @param userRepo                    {@link UserRepo}
     * @param repo                        {@link RestorePasswordEmailRepo}
     * @param restorePasswordEmailService {@link RestorePasswordEmailService}
     */
    @Autowired
    public RestoreLogicServiceImpl(UserRepo userRepo,
                                   RestorePasswordEmailRepo repo,
                                   OwnSecurityService ownSecurityService,
                                   RestorePasswordEmailService restorePasswordEmailService) {
        this.restorePasswordEmailService = restorePasswordEmailService;
        this.userRepo = userRepo;
        this.repo = repo;
        this.ownSecurityService = ownSecurityService;
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public void sendEmailForRestore(String email) {
        log.info("start");
        User user = userRepo
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        RestorePasswordEmail restorePasswordEmail = user.getRestorePasswordEmail();
        if (restorePasswordEmail != null) {
            throw new BadEmailException(PASSWORD_RESTORE_LINK_ALREADY_SENT + email);
        }
        restorePasswordEmailService.save(user);
        log.info("end");
    }


    /**
     * Deletes expiry reset tokens.
     *
     * @author Yurii Koval
     */
    @Override
    @Scheduled(fixedRate = 86400000)
    public void deleteExpiry() {
        int n = restorePasswordEmailService.deleteAllExpiredPasswordResetTokens();
        log.info(n + " password reset tokens were deleted.");
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Transactional
    @Override
    public void restoreByToken(String token, String password) {
        RestorePasswordEmail restorePasswordEmail = repo
                .findByToken(token)
                .orElseThrow(() -> new BadVerifyEmailTokenException(NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN));
        User user = userRepo
                .findById(restorePasswordEmail.getUser().getId())
                .orElseThrow(
                        () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID
                                + restorePasswordEmail.getUser().getId())
                );
        if (restorePasswordEmailService.isNotExpired(restorePasswordEmail.getExpiryDate())) {
            log.info("Date of user email is valid and user was found.");
            ownSecurityService.updatePassword(password, user.getId());
            restorePasswordEmailService.delete(restorePasswordEmail);
        } else {
            log.info("User late with sendEmailForRestore. Token is invalid.");
            throw new UserActivationEmailTokenExpiredException(EMAIL_TOKEN_EXPIRED);
        }
    }
}

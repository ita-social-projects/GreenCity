package greencity.security.service.impl;

import static greencity.constant.ErrorMessage.*;

import greencity.constant.ErrorMessage;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.exception.BadEmailException;
import greencity.exception.BadVerifyEmailTokenException;
import greencity.exception.NotFoundException;
import greencity.exception.UserActivationEmailTokenExpiredException;
import greencity.repository.UserRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.RestoreLogicService;
import greencity.security.service.RestorePasswordEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class RestoreLogicServiceImpl implements RestoreLogicService {
    private RestorePasswordEmailService restorePasswordEmailService;
    private UserRepo userRepo;
    private RestorePasswordEmailRepo repo;
    private OwnSecurityService ownSecurityService;

    /**
     * Constructor for RestoreLogicServiceImpl class.
     *
     * @param userRepo                    {@link UserRepo}
     * @param repo                        {@link RestorePasswordEmailRepo}
     * @param restorePasswordEmailService {@link RestorePasswordEmailService}
     */
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
        User user = userRepo.findByEmail(email).orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        RestorePasswordEmail restorePasswordEmail = repo.findByUser(user);
        if (restorePasswordEmail != null) {
            throw new BadEmailException(PASSWORD_RESTORE_LINK_ALREADY_SENT + email);
        }
        restorePasswordEmailService.save(user);
        log.info("end");
    }


    /**
     * Method to deleting expiry restore tokens.
     *
     * @author Dmytro Dovhal
     */
    @Scheduled(fixedRate = 86400000)
    public void deleteExpiry() {
        log.info("begin");
        restorePasswordEmailService
            .findAll()
            .forEach(
                restore -> {
                    if (!restorePasswordEmailService.isDateValidate(restore.getExpiryDate())) {
                        restorePasswordEmailService.delete(restore);
                    }
                });
        log.info("end");
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Transactional
    @Override
    public void restoreByToken(String token, String password) {
        log.info("begin");
        RestorePasswordEmail restorePasswordEmail =
            repo.findByToken(token)
                .orElseThrow(
                    () -> new BadVerifyEmailTokenException(NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN));
        User user =
            userRepo.findById(restorePasswordEmail.getUser().getId())
                .orElseThrow(() -> new NotFoundException(
                    ErrorMessage.USER_NOT_FOUND_BY_ID + restorePasswordEmail.getUser().getId()));
        if (restorePasswordEmailService.isDateValidate(restorePasswordEmail.getExpiryDate())) {
            log.info("Date of user email is valid and user was found.");
            ownSecurityService.updatePassword(password, user.getId());
            restorePasswordEmailService.delete(restorePasswordEmail);
        } else {
            log.info("User late with sendEmailForRestore. Token is invalid.");
            throw new UserActivationEmailTokenExpiredException(EMAIL_TOKEN_EXPIRED);
        }
        log.info("end");
    }
}

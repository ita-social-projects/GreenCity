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
import greencity.security.jwt.JwtTool;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.RestoreLogicService;
import greencity.security.service.RestorePasswordEmailService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class RestoreLogicServiceImpl implements RestoreLogicService {
    private final RestorePasswordEmailService restorePasswordEmailService;
    private final UserRepo userRepo;
    private final RestorePasswordEmailRepo repo;
    private final OwnSecurityService ownSecurityService;
    private final JwtTool jwtTool;

    /**
     * Constructor for RestoreLogicServiceImpl class.
     * @param userRepo {@link UserRepo}
     * @param repo {@link RestorePasswordEmailRepo}
     * @param restorePasswordEmailService {@link RestorePasswordEmailService}
     * @param jwtTool {@link JwtTool}
     */
    @Autowired
    public RestoreLogicServiceImpl(UserRepo userRepo,
                                   RestorePasswordEmailRepo repo,
                                   OwnSecurityService ownSecurityService,
                                   RestorePasswordEmailService restorePasswordEmailService, JwtTool jwtTool) {
        this.restorePasswordEmailService = restorePasswordEmailService;
        this.userRepo = userRepo;
        this.repo = repo;
        this.ownSecurityService = ownSecurityService;
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
        restorePasswordEmailService.savePasswordRestorationTokenForUser(user, jwtTool.generateTokenKey());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void restoreByToken(String token, String password) {
        RestorePasswordEmail restorePasswordEmail = repo
                .findByToken(token)
                .orElseThrow(() -> new BadVerifyEmailTokenException(NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN));
        if (isNotExpired(restorePasswordEmail.getExpiryDate())) {
            ownSecurityService.updatePassword(password, restorePasswordEmail.getUser().getId());
            restorePasswordEmailService.delete(restorePasswordEmail);
            log.info("User has successfully restore the password.");
        } else {
            log.info("Password restoration token has expired.");
            throw new UserActivationEmailTokenExpiredException(EMAIL_TOKEN_EXPIRED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotExpired(LocalDateTime emailExpiredDate) {
        return LocalDateTime.now().isBefore(emailExpiredDate);
    }
}

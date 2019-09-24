package greencity.security.service.impl;

import static greencity.constant.ErrorMessage.EMAIL_TOKEN_EXPIRED;
import static greencity.constant.ErrorMessage.NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN;

import greencity.constant.ErrorMessage;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.exception.BadVerifyEmailTokenException;
import greencity.exception.NotFoundException;
import greencity.exception.UserActivationEmailTokenExpiredException;
import greencity.repository.UserRepo;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.RestoreLogicService;
import greencity.security.service.RestorePasswordEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class RestoreLogicServiceImpl implements RestoreLogicService {
    private RestorePasswordEmailService restorePasswordEmailService;
    private UserRepo userRepo;
    private RestorePasswordEmailRepo repo;
    private PasswordEncoder passwordEncoder;
    private OwnSecurityRepo ownSecurityRepo;

    public RestoreLogicServiceImpl(UserRepo userRepo,
                                   RestorePasswordEmailRepo repo,
                                   OwnSecurityRepo ownSecurityRepo,
                                   PasswordEncoder passwordEncoder,
                                   RestorePasswordEmailService restorePasswordEmailService) {
        this.restorePasswordEmailService = restorePasswordEmailService;
        this.userRepo = userRepo;
        this.repo = repo;
        this.ownSecurityRepo = ownSecurityRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void sendEmailForRestore(String email) {
        log.info("start");
        User user = userRepo.findByEmail(email).orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        restorePasswordEmailService.save(user);
        log.info("end");
    }


    @Scheduled(fixedRate = 86400000)
    @Override
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
            updatePassword(password, user.getId());
            restorePasswordEmailService.delete(restorePasswordEmail);
        } else {
            log.info("User late with sendEmailForRestore. Token is invalid.");
            throw new UserActivationEmailTokenExpiredException(EMAIL_TOKEN_EXPIRED);
        }
        log.info("end");
    }


    public void updatePassword(String pass, Long id) {
        String password = passwordEncoder.encode(pass);
        ownSecurityRepo.updatePassword(password, id);
    }
}

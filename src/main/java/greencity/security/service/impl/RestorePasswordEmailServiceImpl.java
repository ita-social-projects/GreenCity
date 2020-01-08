package greencity.security.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.RestorePasswordEmailService;
import greencity.service.EmailService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RestorePasswordEmailServiceImpl implements RestorePasswordEmailService {
    private final Integer expirationTime;
    private final RestorePasswordEmailRepo restorePasswordEmailRepo;
    private final EmailService emailService;


    /**
     * Constructor for RestorePasswordEmailServiceImpl class.
     *
     * @param expirationTime server address
     * @param repo           {@link RestorePasswordEmailRepo}
     * @param emailService   {@link EmailService}
     */
    @Autowired
    public RestorePasswordEmailServiceImpl(@Value("${verifyEmailTimeHour}") Integer expirationTime,
                                           RestorePasswordEmailRepo repo,
                                           EmailService emailService) {
        this.expirationTime = expirationTime;
        this.restorePasswordEmailRepo = repo;
        this.emailService = emailService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void savePasswordRestorationTokenForUser(User user, String token) {
        RestorePasswordEmail restorePasswordEmail =
            RestorePasswordEmail.builder()
                .user(user)
                .token(token)
                .expiryDate(calculateExpiryDate(expirationTime))
                .build();
        restorePasswordEmailRepo.save(restorePasswordEmail);
        emailService.sendRestoreEmail(user, restorePasswordEmail.getToken());
    }

    private LocalDateTime calculateExpiryDate(Integer expirationTimeInHour) {
        LocalDateTime now = LocalDateTime.now();
        return now.plusHours(expirationTimeInHour);
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public void delete(RestorePasswordEmail restorePasswordEmail) {
        if (!restorePasswordEmailRepo.existsById(restorePasswordEmail.getId())) {
            throw new NotFoundException(ErrorMessage.LINK_FOR_RESTORE_NOT_FOUND
                    + restorePasswordEmail.getUser().getEmail()
            );
        }
        restorePasswordEmailRepo.delete(restorePasswordEmail);
    }

    /**
     * {@inheritDoc}
     */
    @Scheduled(fixedRate = 86400000)
    @Override
    public void deleteAllExpiredPasswordResetTokens() {
        int rows = restorePasswordEmailRepo.deleteAllExpiredPasswordResetTokens();
        log.info(rows + " password reset tokens were deleted.");
    }
}

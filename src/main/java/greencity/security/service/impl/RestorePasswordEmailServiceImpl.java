package greencity.security.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.RestorePasswordEmailService;
import greencity.service.EmailService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RestorePasswordEmailServiceImpl implements RestorePasswordEmailService {
    private final Integer expireTime;
    private final RestorePasswordEmailRepo restorePasswordEmailRepo;
    private final EmailService emailService;


    /**
     * Constructor for RestorePasswordEmailServiceImpl class.
     *
     * @param expireTime server address
     * @param repo           {@link RestorePasswordEmailRepo}
     * @param emailService   {@link EmailService}
     */
    @Autowired
    public RestorePasswordEmailServiceImpl(@Value("${verifyEmailTimeHour}") Integer expireTime,
                                           RestorePasswordEmailRepo repo,
                                           EmailService emailService) {
        this.expireTime = expireTime;
        this.restorePasswordEmailRepo = repo;
        this.emailService = emailService;
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public void save(User user) {
        RestorePasswordEmail restorePasswordEmail =
            RestorePasswordEmail.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(calculateExpiryDate(expireTime))
                .build();
        restorePasswordEmailRepo.save(restorePasswordEmail);
        emailService.sendRestoreEmail(user, restorePasswordEmail.getToken());
        log.info("end");
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public void delete(RestorePasswordEmail restorePasswordEmail) {
        log.info("begin");
        if (!restorePasswordEmailRepo.existsById(restorePasswordEmail.getId())) {
            throw new NotFoundException(ErrorMessage.LINK_FOR_RESTORE_NOT_FOUND
                    + restorePasswordEmail.getUser().getEmail()
            );
        }
        restorePasswordEmailRepo.delete(restorePasswordEmail);
        log.info("end");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteAllExpiredPasswordResetTokens() {
        return restorePasswordEmailRepo.deleteAllExpiredPasswordResetTokens();
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public List<RestorePasswordEmail> findAll() {
        return restorePasswordEmailRepo.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * @author Yurii Koval
     */
    @Override
    public boolean isNotExpired(LocalDateTime emailExpiredDate) {
        return LocalDateTime.now().isBefore(emailExpiredDate);
    }

    private LocalDateTime calculateExpiryDate(Integer expiryTimeInHour) {
        log.info("start");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime result = now.plusHours(expiryTimeInHour);
        log.info("finish");
        return result;
    }
}

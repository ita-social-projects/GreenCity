package greencity.security.service.impl;

import static greencity.constant.ErrorMessage.*;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.exception.exceptions.BadIdException;
import greencity.exception.exceptions.BadVerifyEmailTokenException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.security.repository.VerifyEmailRepo;
import greencity.security.service.VerifyEmailService;
import greencity.service.EmailService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * {@inheritDoc}
 */
@Service
@Slf4j
public class VerifyEmailServiceImpl implements VerifyEmailService {
    private final Integer expireTime;
    private final VerifyEmailRepo verifyEmailRepo;
    private final EmailService emailService;

    /**
     * Constructor.
     *
     * @param expireTime - how many hours a token will live.
     * @param verifyEmailRepo {@link VerifyEmailRepo} - this is repository for {@link VerifyEmail}
     * @param emailService {@link EmailService} - service for sending email
     */
    @Autowired
    public VerifyEmailServiceImpl(@Value("${verifyEmailTimeHour}") Integer expireTime,
                                  VerifyEmailRepo verifyEmailRepo,
                                  EmailService emailService) {
        this.expireTime = expireTime;
        this.verifyEmailRepo = verifyEmailRepo;
        this.emailService = emailService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(User user) {
        VerifyEmail verifyEmail =
            VerifyEmail.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(calculateExpiryDate(expireTime))
                .build();
        verifyEmailRepo.save(verifyEmail);
        emailService.sendVerificationEmail(user, verifyEmail.getToken());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verifyByToken(String token) {
        VerifyEmail verifyEmail = verifyEmailRepo
                .findByToken(token)
                .orElseThrow(() -> new BadVerifyEmailTokenException(NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN));
        if (isNotExpired(verifyEmail.getExpiryDate())) {
            log.info("Date of user email is valid.");
            delete(verifyEmail);
        } else {
            log.info("User late with verify. Token is invalid.");
            throw new UserActivationEmailTokenExpiredException(EMAIL_TOKEN_EXPIRED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VerifyEmail> findAll() {
        return verifyEmailRepo.findAll();
    }

    private LocalDateTime calculateExpiryDate(Integer expiryTimeInHour) {
        LocalDateTime now = LocalDateTime.now();
        return now.plusHours(expiryTimeInHour);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotExpired(LocalDateTime emailExpiredDate) {
        return LocalDateTime.now().isBefore(emailExpiredDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(VerifyEmail verifyEmail) {
        if (!verifyEmailRepo.existsById(verifyEmail.getId())) {
            throw new BadIdException(NO_ANY_VERIFY_EMAIL_TO_DELETE + verifyEmail.getId());
        }
        verifyEmailRepo.delete(verifyEmail);
    }

    /**
     * {@inheritDoc}
     */
    public int deleteAllExpiredEmailVerificationTokens() {
        return verifyEmailRepo.deleteAllExpiredEmailVerificationTokens();
    }
}

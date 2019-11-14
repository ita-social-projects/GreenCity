package greencity.security.service.impl;

import static greencity.constant.ErrorMessage.*;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.exception.BadIdException;
import greencity.exception.BadVerifyEmailTokenException;
import greencity.exception.UserActivationEmailTokenExpiredException;
import greencity.security.repository.VerifyEmailRepo;
import greencity.security.service.VerifyEmailService;
import greencity.service.EmailService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * {@inheritDoc}
 */
@Service
@Slf4j
public class VerifyEmailServiceImpl implements VerifyEmailService {
    /**
     * Time for validation email token.
     */
    @Value("${verifyEmailTimeHour}")
    private Integer expireTime;

    @Value("${address}")
    private String serverAddress;

    private VerifyEmailRepo verifyEmailRepo;

    private EmailService emailService;


    /**
     * Constructor.
     *
     * @param verifyEmailRepo         {@link VerifyEmailRepo} - this is repository for {@link VerifyEmail}
     * @param emailService {@link EmailService} - service for sending email
     */
    public VerifyEmailServiceImpl(VerifyEmailRepo verifyEmailRepo, EmailService emailService) {
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
        if (isDateValidate(verifyEmail.getExpiryDate())) {
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
    public boolean isDateValidate(LocalDateTime emailExpiredDate) {
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
}

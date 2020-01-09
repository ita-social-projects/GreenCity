package greencity.security.service.impl;

import static greencity.constant.ErrorMessage.*;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.exception.exceptions.BadVerifyEmailTokenException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.security.repository.VerifyEmailRepo;
import greencity.security.service.VerifyEmailService;
import greencity.service.EmailService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * {@inheritDoc}
 */
@Service
@Slf4j
public class VerifyEmailServiceImpl implements VerifyEmailService {
    private final VerifyEmailRepo verifyEmailRepo;
    private final EmailService emailService;

    /**
     * Constructor.
     *
     * @param verifyEmailRepo {@link VerifyEmailRepo}
     * @param emailService {@link EmailService} - a service for sending emails.
     */
    @Autowired
    public VerifyEmailServiceImpl(VerifyEmailRepo verifyEmailRepo,
                                  EmailService emailService) {
        this.verifyEmailRepo = verifyEmailRepo;
        this.emailService = emailService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmail(User user, String token) {
        emailService.sendVerificationEmail(user, token);
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
            verifyEmailRepo.delete(verifyEmail);
            log.info("User has successfully verify the email by token {}.", token);
        } else {
            log.info("User didn't verify his/her email on time with token {}.", token);
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

    /**
     * {@inheritDoc}
     */
    @Scheduled(fixedRate = 86400000)
    @Transactional
    public void deleteAllUsersThatDidNotVerifyEmail() {
        int rows = verifyEmailRepo.deleteAllUsersThatDidNotVerifyEmail();
        log.info(rows + " email verification tokens were deleted.");
    }
}

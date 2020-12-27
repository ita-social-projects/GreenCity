package greencity.security.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.VerifyEmail;
import greencity.exception.exceptions.BadVerifyEmailTokenException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.security.repository.VerifyEmailRepo;
import greencity.security.service.VerifyEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * {@inheritDoc}
 */
@Service
@Slf4j
public class VerifyEmailServiceImpl implements VerifyEmailService {
    private final VerifyEmailRepo verifyEmailRepo;

    /**
     * Constructor.
     *
     * @param verifyEmailRepo {@link VerifyEmailRepo}
     */
    @Autowired
    public VerifyEmailServiceImpl(VerifyEmailRepo verifyEmailRepo) {
        this.verifyEmailRepo = verifyEmailRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Boolean verifyByToken(Long userId, String token) {
        VerifyEmail verifyEmail = verifyEmailRepo
            .findByTokenAndUserId(userId, token)
            .orElseThrow(() -> new BadVerifyEmailTokenException(ErrorMessage.NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN));
        if (isNotExpired(verifyEmail.getExpiryDate())) {
            int rows = verifyEmailRepo.deleteVerifyEmailByTokenAndUserId(userId, token);
            log.info("User has successfully verify the email by token {}. Records deleted {}.", token, rows);
            return true;
        } else {
            log.info("User didn't verify his/her email on time with token {}.", token);
            throw new UserActivationEmailTokenExpiredException(ErrorMessage.EMAIL_TOKEN_EXPIRED);
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

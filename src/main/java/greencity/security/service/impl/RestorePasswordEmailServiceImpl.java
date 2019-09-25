package greencity.security.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.exception.NotFoundException;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.RestorePasswordEmailService;
import greencity.service.EmailService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RestorePasswordEmailServiceImpl implements RestorePasswordEmailService {
    /**
     * Time for validation email token.
     */
    @Value("${verifyEmailTimeHour}")
    private Integer expireTime;

    /**
     * This is server address. We send it to user email. And user can simply submit it.
     */
    @Value("${address}")
    private String serverAddress;

    private RestorePasswordEmailRepo repo;

    private JavaMailSender javaMailSender;

    private EmailService emailService;


    /**
     * Constructor for RestorePasswordEmailServiceImpl class.
     *
     * @param repo           {@link RestorePasswordEmailRepo}
     * @param javaMailSender {@link JavaMailSender}
     * @param emailService   {@link EmailService}
     */
    public RestorePasswordEmailServiceImpl(RestorePasswordEmailRepo repo, JavaMailSender javaMailSender,
                                           EmailService emailService) {
        this.javaMailSender = javaMailSender;
        this.repo = repo;
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
        repo.save(restorePasswordEmail);
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
        if (!repo.existsById(restorePasswordEmail.getId())) {
            throw new NotFoundException(ErrorMessage.LINK_FOR_RESTORE_NOT_FOUND
                + restorePasswordEmail.getUser().getEmail());
        }
        repo.delete(restorePasswordEmail);
        log.info("end");
    }


    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public List<RestorePasswordEmail> findAll() {
        return repo.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public boolean isDateValidate(LocalDateTime emailExpiredDate) {
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

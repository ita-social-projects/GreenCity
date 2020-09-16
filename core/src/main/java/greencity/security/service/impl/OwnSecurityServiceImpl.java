package greencity.security.service.impl;

import greencity.constant.AppConstant;
import static greencity.constant.ErrorMessage.*;
import static greencity.constant.RabbitConstants.SEND_USER_APPROVAL_ROUTING_KEY;
import static greencity.constant.RabbitConstants.VERIFY_EMAIL_ROUTING_KEY;
import greencity.dto.user.UserManagementDto;
import greencity.entity.OwnSecurity;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.exceptions.*;
import greencity.message.UserApprovalMessage;
import greencity.message.VerifyEmailMessage;
import greencity.security.dto.AccessRefreshTokensDto;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.SuccessSignUpDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.OwnSecurityService;
import greencity.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Slf4j
public class OwnSecurityServiceImpl implements OwnSecurityService {
    private final OwnSecurityRepo ownSecurityRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;
    private final Integer expirationTime;
    private final RabbitTemplate rabbitTemplate;
    private final String defaultProfilePicture;
    private final RestorePasswordEmailRepo restorePasswordEmailRepo;
    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;

    /**
     * Constructor.
     */
    @Autowired
    public OwnSecurityServiceImpl(OwnSecurityRepo ownSecurityRepo,
                                  UserService userService,
                                  PasswordEncoder passwordEncoder,
                                  JwtTool jwtTool,
                                  @Value("${verifyEmailTimeHour}") Integer expirationTime,
                                  RabbitTemplate rabbitTemplate,
                                  @Value("${defaultProfilePicture}") String defaultProfilePicture,
                                  RestorePasswordEmailRepo restorePasswordEmailRepo) {
        this.ownSecurityRepo = ownSecurityRepo;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTool = jwtTool;
        this.expirationTime = expirationTime;
        this.rabbitTemplate = rabbitTemplate;
        this.defaultProfilePicture = defaultProfilePicture;
        this.restorePasswordEmailRepo = restorePasswordEmailRepo;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Transactional
    @Override
    public SuccessSignUpDto signUp(OwnSignUpDto dto) {
        User user = createNewRegisteredUser(dto, jwtTool.generateTokenKey());
        OwnSecurity ownSecurity = createOwnSecurity(dto, user);
        VerifyEmail verifyEmail = createVerifyEmail(user, jwtTool.generateTokenKey());
        user.setOwnSecurity(ownSecurity);
        user.setVerifyEmail(verifyEmail);
        try {
            User savedUser = userService.save(user);
            rabbitTemplate.convertAndSend(
                sendEmailTopic,
                VERIFY_EMAIL_ROUTING_KEY,
                new VerifyEmailMessage(savedUser.getId(), savedUser.getName(), savedUser.getEmail(),
                    savedUser.getVerifyEmail().getToken())
            );
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyRegisteredException(USER_ALREADY_REGISTERED_WITH_THIS_EMAIL);
        }
        return new SuccessSignUpDto(user.getId(), user.getName(), user.getEmail(), true);
    }

    private User createNewRegisteredUser(OwnSignUpDto dto, String refreshTokenKey) {
        return User.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .dateOfRegistration(LocalDateTime.now())
            .role(ROLE.ROLE_USER)
            .refreshTokenKey(refreshTokenKey)
            .lastVisit(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .profilePicturePath(defaultProfilePicture)
            .rating(AppConstant.DEFAULT_RATING)
            .build();
    }

    private OwnSecurity createOwnSecurity(OwnSignUpDto dto, User user) {
        return OwnSecurity.builder()
            .password(passwordEncoder.encode(dto.getPassword()))
            .user(user)
            .build();
    }

    private VerifyEmail createVerifyEmail(User user, String emailVerificationToken) {
        return VerifyEmail.builder()
            .user(user)
            .token(emailVerificationToken)
            .expiryDate(calculateExpirationDateTime())
            .build();
    }

    private LocalDateTime calculateExpirationDateTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusHours(this.expirationTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuccessSignInDto signIn(final OwnSignInDto dto) {
        User user = userService.findByEmail(dto.getEmail());
        if (user == null) {
            throw new WrongEmailException(USER_NOT_FOUND_BY_EMAIL + dto.getEmail());
        }
        if (!isPasswordCorrect(dto, user)) {
            throw new WrongPasswordException(BAD_PASSWORD);
        }
        if (user.getVerifyEmail() != null) {
            throw new EmailNotVerified("You should verify the email first, check your email box!");
        }
        if (user.getUserStatus() == UserStatus.DEACTIVATED) {
            throw new UserDeactivatedException(USER_DEACTIVATED);
        }
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), true);
    }

    private boolean isPasswordCorrect(OwnSignInDto signInDto, User user) {
        if (user.getOwnSecurity() == null) {
            return false;
        }
        return passwordEncoder.matches(signInDto.getPassword(), user.getOwnSecurity().getPassword());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public AccessRefreshTokensDto updateAccessTokens(String refreshToken) {
        String email;
        try {
            email = jwtTool.getEmailOutOfAccessToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new BadRefreshTokenException(REFRESH_TOKEN_NOT_VALID);
        }
        User user = userService.findByEmail(email);
        checkUserStatus(user);
        String newRefreshTokenKey = jwtTool.generateTokenKey();
        userService.updateUserRefreshToken(newRefreshTokenKey, user.getId());
        if (jwtTool.isTokenValid(refreshToken, user.getRefreshTokenKey())) {
            user.setRefreshTokenKey(newRefreshTokenKey);
            return new AccessRefreshTokensDto(
                jwtTool.createAccessToken(user.getEmail(), user.getRole()),
                jwtTool.createRefreshToken(user)
            );
        }
        throw new BadRefreshTokenException(REFRESH_TOKEN_NOT_VALID);
    }

    private void checkUserStatus(User user) {
        UserStatus status = user.getUserStatus();
        if (status == UserStatus.BLOCKED) {
            throw new UserBlockedException(USER_DEACTIVATED);
        } else if (status == UserStatus.DEACTIVATED) {
            throw new UserDeactivatedException(USER_DEACTIVATED);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public void updatePassword(String pass, Long id) {
        String password = passwordEncoder.encode(pass);
        ownSecurityRepo.updatePassword(password, id);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateCurrentPassword(UpdatePasswordDto updatePasswordDto, String email) {
        User user = userService.findByEmail(email);
        if (!updatePasswordDto.getPassword().equals(updatePasswordDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchesException(PASSWORDS_DO_NOT_MATCHES);
        }
        if (!passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), user.getOwnSecurity().getPassword())) {
            throw new PasswordsDoNotMatchesException(PASSWORD_DOES_NOT_MATCH);
        }
        updatePassword(updatePasswordDto.getPassword(), user.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void managementRegisterUser(UserManagementDto dto) {
        User user = managementCreateNewRegisteredUser(dto, jwtTool.generateTokenKey());
        savePasswordRestorationTokenForUser(user, jwtTool.generateTokenKey());
    }

    private User managementCreateNewRegisteredUser(UserManagementDto dto, String refreshTokenKey) {
        return User.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .dateOfRegistration(LocalDateTime.now())
            .role(dto.getRole())
            .refreshTokenKey(refreshTokenKey)
            .lastVisit(LocalDateTime.now())
            .userStatus(dto.getUserStatus())
            .emailNotification(EmailNotification.DISABLED)
            .profilePicturePath(defaultProfilePicture)
            .rating(AppConstant.DEFAULT_RATING)
            .build();
    }

    private OwnSecurity managementCreateOwnSecurity(String password, User user) {
        return OwnSecurity.builder()
            .password(passwordEncoder.encode(password))
            .user(user)
            .build();
    }

    /**
     * Creates and saves password restoration token for a given user
     * and publishes event of sending password recovery email to the user.
     *
     * @param user  {@link User} - User whose password is to be recovered
     * @param token {@link String} - token for password restoration
     */
    private void savePasswordRestorationTokenForUser(User user, String token) {
        RestorePasswordEmail restorePasswordEmail =
            RestorePasswordEmail.builder()
                .user(user)
                .token(token)
                .expiryDate(calculateExpirationDate(expirationTime))
                .build();
        restorePasswordEmailRepo.save(restorePasswordEmail);
        userService.save(user);
        rabbitTemplate.convertAndSend(
            sendEmailTopic,
            SEND_USER_APPROVAL_ROUTING_KEY,
            new UserApprovalMessage(user.getId(), user.getName(), user.getEmail(),
                token)
        );
    }

    /**
     * Calculates token expiration date. The amount of hours, after which
     * token will be expired, is set by method parameter.
     *
     * @param expirationTimeInHours - Token expiration delay in hours
     * @return {@link LocalDateTime} - Time at which token will be expired
     */
    private LocalDateTime calculateExpirationDate(Integer expirationTimeInHours) {
        LocalDateTime now = LocalDateTime.now();
        return now.plusHours(expirationTimeInHours);
    }
}

package greencity.security.service.impl;

import static greencity.constant.ErrorMessage.*;
import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.*;
import greencity.security.dto.AccessRefreshTokensDto;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.VerifyEmailService;
import greencity.service.UserService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final VerifyEmailService verifyEmailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;

    /**
     * Constructor.
     */
    @Autowired
    public OwnSecurityServiceImpl(OwnSecurityRepo ownSecurityRepo,
                                  UserService userService,
                                  VerifyEmailService verifyEmailService,
                                  PasswordEncoder passwordEncoder,
                                  AuthenticationManager authManager,
                                  JwtTool jwtTool) {
        this.ownSecurityRepo = ownSecurityRepo;
        this.userService = userService;
        this.verifyEmailService = verifyEmailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTool = jwtTool;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void signUp(OwnSignUpDto dto) {
        if (userService.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyRegisteredException(USER_ALREADY_REGISTERED_WITH_THIS_EMAIL);
        }
        User user = createNewRegisteredUser(dto);
        user.setRefreshTokenKey(jwtTool.generateRefreshTokenKey());
        User savedUser = userService.save(user);
        ownSecurityRepo.save(createUserOwnSecurityToUser(dto, savedUser));
        verifyEmailService.save(savedUser);
    }

    private OwnSecurity createUserOwnSecurityToUser(OwnSignUpDto dto, User user) {
        return OwnSecurity.builder()
            .password(passwordEncoder.encode(dto.getPassword()))
            .user(user)
            .build();
    }

    private User createNewRegisteredUser(OwnSignUpDto dto) {
        return User.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .dateOfRegistration(LocalDateTime.now())
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(OwnSecurity userOwnSecurity) {
        if (!ownSecurityRepo.existsById(userOwnSecurity.getId())) {
            throw new BadIdException(NO_ENY_OWN_SECURITY_TO_DELETE + userOwnSecurity.getId());
        }
        ownSecurityRepo.delete(userOwnSecurity);
    }

    /**
     * {@inheritDoc}
     */
    @Scheduled(fixedRate = 86400000)
    @Override
    public void deleteNotActivatedEmails() {
        int rows = verifyEmailService.deleteAllExpiredEmailVerificationTokens();
        log.info(rows + " email verification tokens were deleted.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuccessSignInDto signIn(final OwnSignInDto dto) {
        User user = userService
            .findByEmail(dto.getEmail())
            .filter(u -> isPasswordCorrect(dto, u))
            .orElseThrow(() -> new BadEmailOrPasswordException(BAD_EMAIL_OR_PASSWORD));
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getFirstName(), true);
    }

    private boolean isPasswordCorrect(OwnSignInDto signInDto, User user) {
        return passwordEncoder.matches(signInDto.getPassword(), user.getOwnSecurity().getPassword());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public AccessRefreshTokensDto updateAccessTokens(String refreshToken) {
        String email = jwtTool.getEmailOutOfAccessToken(refreshToken);
        User user = userService
            .findByEmail(email)
            .orElseThrow(() -> new BadEmailException(USER_NOT_FOUND_BY_EMAIL + email));
        checkUserStatus(user);
        String newRefreshTokenKey = jwtTool.generateRefreshTokenKey();
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
        log.info("Updating user password start");
        User user = userService
                .findByEmail(email)
                .orElseThrow(() -> new BadEmailException(USER_NOT_FOUND_BY_EMAIL + email));
        if (!updatePasswordDto.getPassword().equals(updatePasswordDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchesException(PASSWORDS_DO_NOT_MATCHES);
        }
        if (!passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), user.getOwnSecurity().getPassword())) {
            throw new PasswordsDoNotMatchesException(PASSWORD_DOES_NOT_MATCH);
        }
        updatePassword(updatePasswordDto.getPassword(), user.getId());
        log.info("Updating user password end");
    }
}
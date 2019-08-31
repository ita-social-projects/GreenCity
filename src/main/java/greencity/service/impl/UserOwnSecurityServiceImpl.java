package greencity.service.impl;

import static greencity.constant.ErrorMessage.*;

import greencity.dto.userownsecurity.UserRegisterDto;
import greencity.dto.userownsecurity.UserSignInDto;
import greencity.dto.userownsecurity.UserSuccessSignInDto;
import greencity.entity.User;
import greencity.entity.UserOwnSecurity;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadEmailException;
import greencity.exception.BadEmailOrPasswordException;
import greencity.exception.BadIdException;
import greencity.exception.BadRefreshTokenException;
import greencity.repository.UserOwnSecurityRepo;
import greencity.security.JwtTokenTool;
import greencity.service.UserOwnSecurityService;
import greencity.service.UserService;
import greencity.service.VerifyEmailService;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides the class to manage {@link UserOwnSecurityService} entity.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserOwnSecurityServiceImpl implements UserOwnSecurityService {
    private UserOwnSecurityRepo repo;
    private UserService userService;
    private VerifyEmailService verifyEmailService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager manager;
    private JwtTokenTool jwtTokenTool;

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void register(UserRegisterDto dto) {
        log.info("begin");
        User byEmail = userService.findByEmail(dto.getEmail());

        if (byEmail != null) {
            // He has already registered
            if (byEmail.getUserOwnSecurity() == null) {
                // He has already registered by else method of registration
                repo.save(createUserOwnSecurityToUser(dto, byEmail));
                verifyEmailService.save(byEmail);
            } else {
                throw new BadEmailException(USER_ALREADY_REGISTERED_WITH_THIS_EMAIL);
            }
        } else {
            User user = createNewRegisteredUser(dto);
            User savedUser = userService.save(user);
            repo.save(createUserOwnSecurityToUser(dto, savedUser));
            verifyEmailService.save(savedUser);
        }
        log.info("end");
    }

    private UserOwnSecurity createUserOwnSecurityToUser(UserRegisterDto dto, User user) {
        return UserOwnSecurity.builder()
            .password(passwordEncoder.encode(dto.getPassword()))
            .user(user)
            .build();
    }

    private User createNewRegisteredUser(UserRegisterDto dto) {
        return User.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .dateOfRegistration(LocalDateTime.now())
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(UserOwnSecurity userOwnSecurity) {
        log.info("begin");
        if (!repo.existsById(userOwnSecurity.getId())) {
            throw new BadIdException(NO_ENY_USER_OWN_SECURITY_TO_DELETE + userOwnSecurity.getId());
        }
        repo.delete(userOwnSecurity);
        log.info("end");
    }

    /**
     * {@inheritDoc}
     */
    @Scheduled(fixedRate = 86400000)
    @Override
    public void deleteNotActiveEmailUsers() {
        // 86400000 - доба
        log.info("begin");
        verifyEmailService
            .findAll()
            .forEach(
                verifyEmail -> {
                    if (verifyEmailService.isDateValidate(verifyEmail.getExpiryDate())) {
                        delete(verifyEmail.getUser().getUserOwnSecurity());
                        verifyEmailService.delete(verifyEmail);
                        userService.deleteById(verifyEmail.getUser().getId());
                    }
                });
        log.info("end");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserSuccessSignInDto signIn(UserSignInDto dto) {
        // This method will be change when we will add security
        log.info("begin");
        try {
            manager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
            User byEmail = userService.findByEmail(dto.getEmail());

            String accessToken =
                jwtTokenTool.createAccessToken(byEmail.getEmail(), byEmail.getRole());
            String refreshToken = jwtTokenTool.createRefreshToken(byEmail.getEmail());
            log.info("end");
            return new UserSuccessSignInDto(accessToken, refreshToken);
        } catch (AuthenticationException e) {
            throw new BadEmailOrPasswordException(BAD_EMAIL_OR_PASSWORD);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String updateAccessToken(String refreshToken) {
        if (jwtTokenTool.isTokenValid(refreshToken)) {
            String email = jwtTokenTool.getEmailByToken(refreshToken);
            User user = userService.findByEmail(email);
            if (user != null) {
                return jwtTokenTool.createAccessToken(user.getEmail(), user.getRole());
            }
        }
        throw new BadRefreshTokenException(REFRESH_TOKEN_NOT_VALID);
    }
}

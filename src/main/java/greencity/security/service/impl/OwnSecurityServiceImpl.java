package greencity.security.service.impl;

import java.time.LocalDateTime;

import greencity.security.dto.own_security.OwnSignInDto;
import greencity.security.dto.own_security.OwnSignUpDto;
import greencity.security.dto.SuccessSignInDto;
import greencity.entity.User;
import greencity.entity.OwnSecurity;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadEmailException;
import greencity.exception.BadEmailOrPasswordException;
import greencity.exception.BadIdException;
import greencity.exception.BadRefreshTokenException;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.jwt.JwtTokenTool;
import greencity.security.service.OwnSecurityService;
import greencity.service.UserService;
import greencity.security.service.VerifyEmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static greencity.constant.ErrorMessage.*;

/**
 * Provides the class to manage {@link OwnSecurityService} entity.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class OwnSecurityServiceImpl implements OwnSecurityService {

    private OwnSecurityRepo repo;
    private UserService userService;
    private VerifyEmailService verifyEmailService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager manager;
    private JwtTokenTool jwtTokenTool;

    /** {@inheritDoc} */
    @Transactional
    @Override
    public void register(OwnSignUpDto dto) {
        log.info("begin");
        User byEmail = userService.findByEmail(dto.getEmail());

        if (byEmail != null) {
            // He has already registered
            if (byEmail.getOwnSecurity() == null) {
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
                .role(ROLE.USER_ROLE)
                .lastVisit(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVATED)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(OwnSecurity ownSecurity) {
        log.info("begin");
        if (!repo.existsById(ownSecurity.getId())) {
            throw new BadIdException(NO_ENY_USER_OWN_SECURITY_TO_DELETE + ownSecurity.getId());
        }
        repo.delete(ownSecurity);
        log.info("end");
    }

    /** {@inheritDoc} */
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
                                delete(verifyEmail.getUser().getOwnSecurity());
                                verifyEmailService.delete(verifyEmail);
                                userService.deleteById(verifyEmail.getUser().getId());
                            }
                        });
        log.info("end");
    }

    /** {@inheritDoc} */
    @Override
    public SuccessSignInDto signIn(OwnSignInDto dto) {
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
            return new SuccessSignInDto(accessToken, refreshToken);
        } catch (AuthenticationException e) {
            throw new BadEmailOrPasswordException(BAD_EMAIL_OR_PASSWORD);
        }
    }

    /** {@inheritDoc} */
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

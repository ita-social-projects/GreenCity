package greencity.security.service.impl;

import static greencity.constant.AppConstant.GOOGLE_FAMILY_NAME;
import static greencity.constant.AppConstant.GOOGLE_GIVEN_NAME;
import static greencity.constant.ErrorMessage.BAD_GOOGLE_TOKEN;
import static greencity.constant.ErrorMessage.USER_DEACTIVATED;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.UserDeactivatedException;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTokenTool;
import greencity.security.service.GoogleSecurityService;
import greencity.service.UserService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Slf4j
public class GoogleSecurityServiceImpl implements GoogleSecurityService {
    private UserService userService;
    private GoogleIdTokenVerifier verifier;
    private JwtTokenTool tokenTool;

    /**
     * Constructor.
     *
     * @param userService {@link UserService} - service of {@link User} logic.
     * @param tokenTool   {@link JwtTokenTool} - tool for jwt logic.
     * @param clientId    {@link String} - google client id.
     */
    public GoogleSecurityServiceImpl(UserService userService,
                                     JwtTokenTool tokenTool,
                                     @Value("${google.clientId}") String clientId
    ) {
        this.userService = userService;
        this.tokenTool = tokenTool;
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
            .setAudience(
                Collections.singletonList(clientId)
            )
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public SuccessSignInDto authenticate(String idToken) {
        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();
                String email = payload.getEmail();
                String familyName = (String) payload.get(GOOGLE_FAMILY_NAME);
                String givenName = (String) payload.get(GOOGLE_GIVEN_NAME);
                Optional<User> byEmail = userService.findByEmail(email);
                if (byEmail.isPresent()) {
                    User user = byEmail.get();
                    if (user.getUserStatus() == UserStatus.DEACTIVATED) {
                        throw new UserDeactivatedException(USER_DEACTIVATED);
                    }
                    log.info("Google sign-in exist user - {}", user.getEmail());
                    return getSuccessSignInDto(user);
                } else {
                    User user = createNewUser(email, familyName, givenName);
                    userService.save(user);
                    log.info("Google sign-up and sign-in user - {}", user.getEmail());
                    return getSuccessSignInDto(user);
                }
            }
            throw new IllegalArgumentException(BAD_GOOGLE_TOKEN);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException(BAD_GOOGLE_TOKEN + ". " + e.getMessage());
        }
    }

    private User createNewUser(String email, String familyName, String givenName) {
        return User.builder()
            .email(email)
            .lastName(familyName)
            .firstName(givenName)
            .role(ROLE.ROLE_USER)
            .dateOfRegistration(LocalDateTime.now())
            .lastVisit(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .build();
    }

    private SuccessSignInDto getSuccessSignInDto(User user) {
        String accessToken = tokenTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = tokenTool.createRefreshToken(user.getEmail());
        return new SuccessSignInDto(accessToken, refreshToken, user.getFirstName());
    }
}

package greencity.security.service.impl;

import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.security.service.FacebookSecurityService;
import greencity.service.UserService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static greencity.constant.ErrorMessage.BAD_FACEBOOK_TOKEN;

/**
 * {@inheritDoc}
 */
@Service
@Slf4j
public class FacebookSecurityServiceImpl implements FacebookSecurityService {
    @Value("${address}")
    private String address;
    @Value("${spring.social.facebook.app-id}")
    private String facebookAppId;
    @Value("${spring.social.facebook.app-secret}")
    private String facebookAppSecret;

    private final UserService userService;
    private final JwtTool jwtTool;

    /**
     * Constructor.
     *
     * @param userService {@link UserService} - service of {@link User} logic.
     * @param jwtTool     {@link JwtTool} - tool for jwt logic.
     */
    @Autowired
    public FacebookSecurityServiceImpl(UserService userService,
                                       JwtTool jwtTool) {
        this.userService = userService;
        this.jwtTool = jwtTool;
    }

    /**
     * {@inheritDoc}
     */
    private FacebookConnectionFactory createFacebookConnection() {
        return new FacebookConnectionFactory(facebookAppId, facebookAppSecret);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public String generateFacebookAuthorizeURL() {
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri(address + "/facebookSecurity/facebook");
        params.setScope("email");
        return createFacebookConnection()
            .getOAuthOperations()
            .buildAuthenticateUrl(params);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Transactional
    @Override
    public SuccessSignInDto generateFacebookAccessToken(String code) {
        String accessToken = createFacebookConnection()
            .getOAuthOperations()
            .exchangeForAccess(code, address + "/facebookSecurity/facebook", null)
            .getAccessToken();
        if (accessToken != null) {
            Facebook facebook = new FacebookTemplate(accessToken);
            User byEmail = facebook.fetchObject("me", User.class, "name", "email");
            String email = byEmail.getEmail();
            log.info(email);
            String name = byEmail.getName();
            log.info(name);
            byEmail = userService.findByEmail(email);
            User user = byEmail;
            if (user == null) {
                user = createNewUser(email, name);
                User savedUser = userService.save(user);
                log.info("Facebook sign-up and sign-in user - {}", user.getEmail());
                return getSuccessSignInDto(user);
            } else {
                log.info("Facebook sign-in exist user - {}", user.getEmail());
                return getSuccessSignInDto(user);
            }
        } else {
            throw new IllegalArgumentException(BAD_FACEBOOK_TOKEN);
        }
    }

    private User createNewUser(String email, String userName) {
        return User.builder()
            .email(email)
            .name(userName)
            .role(ROLE.ROLE_USER)
            .dateOfRegistration(LocalDateTime.now())
            .lastVisit(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .refreshTokenKey(jwtTool.generateTokenKey())
            .build();
    }

    private SuccessSignInDto getSuccessSignInDto(User user) {
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
    }
}

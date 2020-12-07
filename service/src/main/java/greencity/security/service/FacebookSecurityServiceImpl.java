package greencity.security.service;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static greencity.constant.AppConstant.REGISTRATION_EMAIL_FIELD_NAME;

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
    private final ModelMapper modelMapper;

    /**
     * Constructor.
     *
     * @param userService {@link UserService} - service of {@link User} logic.
     * @param jwtTool     {@link JwtTool} - tool for jwt logic.
     */
    @Autowired
    public FacebookSecurityServiceImpl(UserService userService,
        JwtTool jwtTool,
        ModelMapper modelMapper) {
        this.userService = userService;
        this.jwtTool = jwtTool;
        this.modelMapper = modelMapper;
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
            UserVO byEmail = facebook.fetchObject(AppConstant.FACEBOOK_OBJECT_ID, UserVO.class, AppConstant.USERNAME,
                REGISTRATION_EMAIL_FIELD_NAME);
            String email = byEmail.getEmail();
            log.info(email);
            String name = byEmail.getName();
            log.info(name);
            byEmail = userService.findByEmail(email);
            UserVO user = byEmail;
            if (user == null) {
                user = modelMapper.map(createNewUser(email, name), UserVO.class);
                log.info("Facebook sign-up and sign-in user - {}", user.getEmail());
                return getSuccessSignInDto(user);
            } else {
                log.info("Facebook sign-in exist user - {}", user.getEmail());
                return getSuccessSignInDto(user);
            }
        } else {
            throw new IllegalArgumentException(ErrorMessage.BAD_FACEBOOK_TOKEN);
        }
    }

    private User createNewUser(String email, String userName) {
        return User.builder()
            .email(email)
            .name(userName)
            .role(Role.ROLE_USER)
            .dateOfRegistration(LocalDateTime.now())
            .lastVisit(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .refreshTokenKey(jwtTool.generateTokenKey())
            .build();
    }

    private SuccessSignInDto getSuccessSignInDto(UserVO user) {
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
    }
}

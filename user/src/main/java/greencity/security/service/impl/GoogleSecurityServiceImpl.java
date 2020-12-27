package greencity.security.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.entity.UserAction;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.repository.UserRepo;
import greencity.security.service.GoogleSecurityService;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.AchievementService;
import greencity.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;

import static greencity.constant.AppConstant.GOOGLE_PICTURE;
import static greencity.constant.AppConstant.USERNAME;
import static greencity.constant.ErrorMessage.USER_NOT_FOUND_BY_EMAIL;
import static greencity.security.service.impl.OwnSecurityServiceImpl.getUserAchievements;
import static greencity.security.service.impl.OwnSecurityServiceImpl.getUserActions;

/**
 * {@inheritDoc}
 */
@Slf4j
@Service
public class GoogleSecurityServiceImpl implements GoogleSecurityService {
    private final UserService userService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final JwtTool jwtTool;
    private final ModelMapper modelMapper;
    private final AchievementService achievementService;
    private final UserRepo userRepo;

    /**
     * Constructor.
     *
     * @param userService           {@link UserService} - service of {@link User}
     *                              logic.
     * @param jwtTool               {@link JwtTool} - tool for jwt logic.
     * @param googleIdTokenVerifier {@link GoogleIdTokenVerifier} - tool for verify
     *                              googleIdToken.
     */
    @Autowired
    public GoogleSecurityServiceImpl(UserService userService,
        JwtTool jwtTool,
        GoogleIdTokenVerifier googleIdTokenVerifier,
        ModelMapper modelMapper,
        AchievementService achievementService,
        UserRepo userRepo) {
        this.userService = userService;
        this.jwtTool = jwtTool;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.modelMapper = modelMapper;
        this.achievementService = achievementService;
        this.userRepo = userRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public SuccessSignInDto authenticate(String idToken) {
        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();
                String email = payload.getEmail();
                String userName = (String) payload.get(USERNAME);
                UserVO userVO = userService.findByEmail(email);
                if (userVO == null) {
                    log.info(USER_NOT_FOUND_BY_EMAIL + email);
                    String profilePicture = (String) payload.get(GOOGLE_PICTURE);
                    User user = createNewUser(email, userName, profilePicture);
                    List<UserAchievement> userAchievementList = createUserAchievements(user);
                    List<UserAction> userActionsList = createUserActions(user);
                    user.setUserAchievements(userAchievementList);
                    user.setUserActions(userActionsList);
                    User savedUser = userRepo.save(user);
                    user.setId(savedUser.getId());
                    userVO = modelMapper.map(user, UserVO.class);
                    log.info("Google sign-up and sign-in user - {}", userVO.getEmail());
                    return getSuccessSignInDto(userVO);
                } else {
                    if (userVO.getUserStatus() == UserStatus.DEACTIVATED) {
                        throw new UserDeactivatedException(ErrorMessage.USER_DEACTIVATED);
                    }
                    log.info("Google sign-in exist user - {}", userVO.getEmail());
                    return getSuccessSignInDto(userVO);
                }
            } else {
                throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN);
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException(ErrorMessage.BAD_GOOGLE_TOKEN + ". " + e.getMessage());
        }
    }

    private User createNewUser(String email, String userName, String profilePicture) {
        return User.builder()
            .email(email)
            .name(userName)
            .firstName(userName)
            .role(Role.ROLE_USER)
            .dateOfRegistration(LocalDateTime.now())
            .lastVisit(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .refreshTokenKey(jwtTool.generateTokenKey())
            .profilePicturePath(profilePicture)
            .rating(AppConstant.DEFAULT_RATING)
            .build();
    }

    private List<UserAchievement> createUserAchievements(User user) {
        return getUserAchievements(user, modelMapper, achievementService);
    }

    private List<UserAction> createUserActions(User user) {
        return getUserActions(user, modelMapper, achievementService);
    }

    private SuccessSignInDto getSuccessSignInDto(UserVO user) {
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
    }
}

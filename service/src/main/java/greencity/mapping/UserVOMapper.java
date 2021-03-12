package greencity.mapping;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class UserVOMapper extends AbstractConverter<User, UserVO> {
    @Override
    protected UserVO convert(User user) {
        return UserVO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .userCredo(user.getUserCredo())
            .firstName(user.getFirstName())
            .emailNotification(user.getEmailNotification())
            .userStatus(user.getUserStatus())
            .lastVisit(user.getLastVisit())
            .rating(user.getRating())
            .verifyEmail(user.getVerifyEmail() != null ? VerifyEmailVO.builder()
                .id(user.getVerifyEmail().getId())
                .user(UserVO.builder()
                    .id(user.getVerifyEmail().getUser().getId())
                    .name(user.getVerifyEmail().getUser().getName())
                    .build())
                .expiryDate(user.getVerifyEmail().getExpiryDate())
                .token(user.getVerifyEmail().getToken())
                .build() : null)
            .userFriends(user.getUserFriends() != null ? user.getUserFriends()
                .stream().map(user1 -> UserVO.builder()
                    .id(user1.getId())
                    .name(user1.getName())
                    .build())
                .collect(Collectors.toList()) : null)
            .refreshTokenKey(user.getRefreshTokenKey())
            .ownSecurity(user.getOwnSecurity() != null ? OwnSecurityVO.builder()
                .id(user.getOwnSecurity().getId())
                .password(user.getOwnSecurity().getPassword())
                .user(UserVO.builder()
                    .id(user.getOwnSecurity().getUser().getId())
                    .email(user.getOwnSecurity().getUser().getEmail())
                    .build())
                .build() : null)
            .dateOfRegistration(user.getDateOfRegistration())
            .profilePicturePath(user.getProfilePicturePath())
            .city(user.getCity())
            .showShoppingList(user.getShowShoppingList())
            .showEcoPlace(user.getShowEcoPlace())
            .showLocation(user.getShowLocation())
            .lastActivityTime(user.getLastActivityTime())
            .userAchievements(user.getUserAchievements() != null ? user.getUserAchievements()
                .stream().map(userAchievement -> UserAchievementVO.builder()
                    .id(userAchievement.getId())
                    .achievementStatus(userAchievement.getAchievementStatus())
                    .user(UserVO.builder()
                        .id(userAchievement.getUser().getId())
                        .build())
                    .achievement(AchievementVO.builder()
                        .id(userAchievement.getAchievement().getId())
                        .build())
                    .build())
                .collect(Collectors.toList()) : new ArrayList<>())
            .userActions(user.getUserActions() != null ? user.getUserActions()
                .stream().map(userAction -> UserActionVO.builder()
                    .id(userAction.getId())
                    .achievementCategory(AchievementCategoryVO.builder()
                        .id(userAction.getAchievementCategory().getId())
                        .build())
                    .count(userAction.getCount())
                    .user(UserVO.builder()
                        .id(userAction.getUser().getId())
                        .build())
                    .build())
                .collect(Collectors.toList()) : new ArrayList<>())
            .build();
    }
}

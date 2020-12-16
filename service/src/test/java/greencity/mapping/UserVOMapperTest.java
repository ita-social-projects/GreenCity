package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserVO;
import greencity.entity.Achievement;
import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.entity.VerifyEmail;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserVOMapperTest {
    @InjectMocks
    UserVOMapper mapper;

    @Test
    void convert() {
        UserVO expected = ModelUtils.getUserVOWithData();

        User userToBeConverted = User.builder()
            .id(expected.getId())
            .name(expected.getName())
            .email(expected.getEmail())
            .role(expected.getRole())
            .userCredo(expected.getUserCredo())
            .firstName(expected.getFirstName())
            .emailNotification(expected.getEmailNotification())
            .userStatus(expected.getUserStatus())
            .lastVisit(expected.getLastVisit())
            .rating(expected.getRating())
            .verifyEmail(expected.getVerifyEmail() != null ? VerifyEmail.builder()
                .id(expected.getVerifyEmail().getId())
                .user(User.builder()
                    .id(expected.getVerifyEmail().getUser().getId())
                    .name(expected.getVerifyEmail().getUser().getName())
                    .build())
                .expiryDate(expected.getVerifyEmail().getExpiryDate())
                .token(expected.getVerifyEmail().getToken())
                .build() : null)
            .userFriends(expected.getUserFriends() != null ? expected.getUserFriends()
                .stream().map(user1 -> User.builder()
                    .id(user1.getId())
                    .name(user1.getName())
                    .build())
                .collect(Collectors.toList()) : null)
            .refreshTokenKey(expected.getRefreshTokenKey())
            .ownSecurity(expected.getOwnSecurity() != null ? OwnSecurity.builder()
                .id(expected.getOwnSecurity().getId())
                .password(expected.getOwnSecurity().getPassword())
                .user(User.builder()
                    .id(expected.getOwnSecurity().getUser().getId())
                    .email(expected.getOwnSecurity().getUser().getEmail())
                    .build())
                .build() : null)
            .dateOfRegistration(expected.getDateOfRegistration())
            .profilePicturePath(expected.getProfilePicturePath())
            .city(expected.getCity())
            .showShoppingList(expected.getShowShoppingList())
            .showEcoPlace(expected.getShowEcoPlace())
            .showLocation(expected.getShowLocation())
            .lastActivityTime(expected.getLastActivityTime())
            .userAchievements(expected.getUserAchievements() != null ? expected.getUserAchievements()
                .stream().map(userAchievement -> UserAchievement.builder()
                    .id(userAchievement.getId())
                    .achievementStatus(userAchievement.getAchievementStatus())
                    .user(User.builder()
                        .id(userAchievement.getUser().getId())
                        .build())
                    .achievement(Achievement.builder()
                        .id(userAchievement.getAchievement().getId())
                        .build())
                    .build())
                .collect(Collectors.toList()) : null)
            .build();


        assertEquals(expected, mapper.convert(userToBeConverted));
    }
}
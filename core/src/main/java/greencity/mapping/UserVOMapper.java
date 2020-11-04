package greencity.mapping;

import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserVOMapper extends AbstractConverter<User, UserVO> {
    @Override
    protected UserVO convert(User user) {
        VerifyEmailVO verifyEmailVO = null;
        if (user.getVerifyEmail() != null) {
            verifyEmailVO = VerifyEmailVO.builder()
                    .id(user.getVerifyEmail().getId())
                    .user(UserVO.builder()
                            .id(user.getVerifyEmail().getUser().getId())
                            .name(user.getVerifyEmail().getUser().getName())
                            .build())
                    .expiryDate(user.getVerifyEmail().getExpiryDate())
                    .token(user.getVerifyEmail().getToken())
                    .build();
        }
        List<UserVO> userFriends = new ArrayList<>();
        if (user.getUserGoals() != null) {
            userFriends = user.getUserFriends()
                    .stream().map(user1 -> UserVO.builder()
                            .id(user1.getId())
                            .name(user1.getName())
                            .build())
                    .collect(Collectors.toList());
        }
        OwnSecurityVO ownSecurityVO = null;
        if (user.getOwnSecurity() != null) {
            ownSecurityVO = OwnSecurityVO.builder()
                    .id(user.getOwnSecurity().getId())
                    .password(user.getOwnSecurity().getPassword())
                    .user(UserVO.builder()
                            .id(user.getOwnSecurity().getUser().getId())
                            .email(user.getOwnSecurity().getUser().getEmail())
                            .build())
                    .build();
        }
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
                .verifyEmail(verifyEmailVO)
                .userFriends(userFriends)
                .refreshTokenKey(user.getRefreshTokenKey())
                .ownSecurity(ownSecurityVO)
                .dateOfRegistration(user.getDateOfRegistration())
                .profilePicturePath(user.getProfilePicturePath())
                .city(user.getCity())
                .showShoppingList(user.getShowShoppingList())
                .showEcoPlace(user.getShowEcoPlace())
                .showLocation(user.getShowLocation())
                .lastActivityTime(user.getLastActivityTime())
                .build();
    }
}

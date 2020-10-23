package greencity.mapping;

import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper extends AbstractConverter<UserVO, User> {
    @Override
    protected User convert(UserVO user) {
        return User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .userCredo(user.getUserCredo())
                .userStatus(user.getUserStatus())
                .lastVisit(user.getLastVisit())
                .rating(user.getRating())
                .verifyEmail(VerifyEmail.builder()
                        .id(user.getVerifyEmail().getId())
                        .expiryDate(user.getVerifyEmail().getExpiryDate())
                        .token(user.getVerifyEmail().getToken())
                        .build())
                .userGoals(user.getUserGoals()
                        .stream().map(userGoal -> UserGoal.builder()
                                .id(userGoal.getId())
                                .dateCompleted(userGoal.getDateCompleted())
                                .status(userGoal.getStatus())
                                .goal(Goal.builder()
                                        .id(userGoal.getGoal().getId())
                                        .build())
                                .user(User.builder()
                                        .id(userGoal.getUser().getId())
                                        .build())
                                .customGoal(CustomGoal.builder()
                                        .id(userGoal.getCustomGoal().getId())
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .customGoals(user.getCustomGoals()
                        .stream().map(customGoal -> CustomGoal.builder()
                                .id(customGoal.getId())
                                .text(customGoal.getText())
                                .build()).collect(Collectors.toList()))
                .userFriends(user.getUserFriends()
                        .stream().map(user1 -> User.builder()
                                .id(user1.getId())
                                .name(user1.getName())
                                .build())
                        .collect(Collectors.toList()))
                .refreshTokenKey(user.getRefreshTokenKey())
                .ownSecurity(OwnSecurity.builder()
                        .id(user.getOwnSecurity().getId())
                        .password(user.getOwnSecurity().getPassword())
                        .user(User.builder()
                                .id(user.getOwnSecurity().getUser().getId())
                                .build())
                        .build())
                .build();
    }
}

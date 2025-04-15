package greencity.mapping;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserVOAchievementMapper extends AbstractConverter<User, UserVOAchievement> {

    private final ModelMapper modelMapper;

    @Override
    protected UserVOAchievement convert(User user) {
        UserVO userVO = modelMapper.map(user, UserVO.class);

        return UserVOAchievement.builder()
            .id(user.getId())
            .name(userVO.getName())
            .userAchievements(userVO.getUserAchievements() != null ? userVO.getUserAchievements()
                .stream().map(userAchievement -> UserAchievementVO.builder()
                    .id(userAchievement.getId())
                    .user(UserVO.builder()
                        .id(userAchievement.getUser().getId())
                        .build())
                    .achievement(AchievementVO.builder()
                        .id(userAchievement.getAchievement().getId())
                        .build())
                    .build())
                .collect(Collectors.toList()) : null)
            .build();
    }
}

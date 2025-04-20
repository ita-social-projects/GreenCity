package greencity.mapping;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class UserVOAchievementMapper extends AbstractConverter<User, UserVOAchievement> {

    private final ModelMapper modelMapper;

    @Lazy
    public UserVOAchievementMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected UserVOAchievement convert(User user) {
        UserVO userVO = modelMapper.map(user, UserVO.class);

        return UserVOAchievement.builder()
            .id(user.getId())
            .name(user.getName())
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

package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserVOAchievementMapperTest {
    @InjectMocks
    private UserVOAchievementMapper userVOAchievementMapper;

    @Test
    void convert() {
        User user = ModelUtils.getUser();
        UserAchievement userAchievements = ModelUtils.getUserAchievement();
        user.setUserAchievements(Collections.singletonList(userAchievements));
        UserVOAchievement expected = UserVOAchievement.builder()
            .id(user.getId())
            .name(user.getName())
            .userAchievements(user.getUserAchievements() != null ? user.getUserAchievements()
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
        assertEquals(expected, userVOAchievementMapper.convert(user));
    }
}

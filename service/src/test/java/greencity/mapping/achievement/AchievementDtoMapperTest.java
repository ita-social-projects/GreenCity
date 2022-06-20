package greencity.mapping.achievement;

import greencity.ModelUtils;
import greencity.dto.achievement.AchievementDto;
import greencity.entity.Achievement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AchievementDtoMapperTest {
    @InjectMocks
    AchievementDtoMapper mapper;

    @Test
    void convert() {
        Achievement achievement = ModelUtils.getAchievement();
        AchievementDto expected = ModelUtils.getAchievementDto();
        AchievementDto actual = mapper.convert(achievement);

        assertEquals(expected, actual);
    }

    @Test
    void convertEmptyTranslationsAndUserAchievements() {
        Achievement achievement = ModelUtils.getAchievement();
        achievement.setTranslations(Collections.emptyList());
        achievement.setUserAchievements(null);

        AchievementDto expected = AchievementDto.builder()
            .id(achievement.getId())
            .icon("https://link.for.file/icon.png")
            .category(achievement.getAchievementCategory().getName())
            .condition(achievement.getCondition()).build();

        AchievementDto actual = mapper.convert(achievement);

        assertEquals(expected, actual);
    }
}
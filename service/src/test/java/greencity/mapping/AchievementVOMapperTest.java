package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.ModelUtils;
import greencity.dto.achievement.AchievementTranslationDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.language.LanguageVO;
import greencity.entity.Achievement;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import greencity.entity.localization.AchievementTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AchievementVOMapperTest {

    @InjectMocks
    AchievementVOMapper achievementVOMapper;

    @Test
    void convert() {
        List<AchievementTranslation> list1 = Arrays.asList(ModelUtils.getAchievementTranslation());
        List<AchievementTranslationDto> list = new ArrayList<>();
        list1
                .forEach(achievementTranslation -> list.add(AchievementTranslationDto.builder()
                        .id(achievementTranslation.getId())
                        .name(achievementTranslation.getName())
                        .nameEng(achievementTranslation.getNameEng())
                        .achievement_id(achievementTranslation.getAchievement().getId())
                        .build()));
        ;
        Achievement achievement = ModelUtils.getAchievement();
        achievement.setTranslations(list1);

        AchievementVO expected = AchievementVO.builder()
                .id(achievement.getId())
                .translations(list)
                .achievementCategory(AchievementCategoryVO.builder()
                        .id(achievement.getAchievementCategory().getId())
                        .name(achievement.getAchievementCategory().getName())
                        .build())
                .build();

        assertEquals(expected, achievementVOMapper.convert(achievement));
    }
}

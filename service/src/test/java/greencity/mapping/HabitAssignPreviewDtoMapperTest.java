package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignPreviewDto;
import greencity.dto.habit.HabitPreviewDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.user.UserVO;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitTranslation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitAssignPreviewDtoMapperTest {

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    HabitAssignPreviewDtoMapper habitAssignPreviewDtoMapper;

    @Test
    void convertTest() {
        UserVO userVO = mock(UserVO.class);
        LanguageDTO language = ModelUtils.getUaLanguageDTO();
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        habitAssign.getHabit().setHabitTranslations(List.of(
            HabitTranslation.builder()
                .id(1L)
                .name("name")
                .habitItem("habitItem")
                .description("description")
                .languageCode("ua")
                .build(),
            HabitTranslation.builder()
                .id(2L)
                .name("name")
                .habitItem("habitItem")
                .description("description")
                .languageCode("en")
                .build()));
        Habit habit = habitAssign.getHabit();
        HabitTranslation habitTranslationUk = habit.getHabitTranslations().stream()
            .filter(translation -> translation.getLanguageCode().equalsIgnoreCase("ua"))
            .findFirst().orElse(null);
        HabitTranslation habitTranslationEn = habit.getHabitTranslations().stream()
            .filter(translation -> !translation.getLanguageCode().equalsIgnoreCase("en"))
            .findFirst().orElse(null);
        HabitTranslationDto habitTranslationDto = HabitTranslationDto.builder()
            .name(habitTranslationEn.getName())
            .habitItem(habitTranslationEn.getHabitItem())
            .description(habitTranslationUk.getDescription())
            .build();
        HabitPreviewDto habitPreviewDto = HabitPreviewDto.builder()
            .id(habit.getId())
            .image(habit.getImage())
            .habitTranslation(habitTranslationDto)
            .build();
        HabitAssignPreviewDto expected = HabitAssignPreviewDto.builder()
            .id(habitAssign.getId())
            .status(habitAssign.getStatus())
            .userId(habitAssign.getUser().getId())
            .duration(habitAssign.getDuration())
            .workingDays(habitAssign.getWorkingDays())
            .habit(habitPreviewDto)
            .build();

        when(modelMapper.map(habitAssign.getUser(), UserVO.class))
            .thenReturn(userVO);
        when(userVO.getLanguageVO())
            .thenReturn(language);

        HabitAssignPreviewDto actual = habitAssignPreviewDtoMapper.convert(habitAssign);

        assertEquals(expected, actual);
    }
}

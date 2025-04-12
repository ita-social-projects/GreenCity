package greencity.mapping;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HabitTranslationMapperTests {
    @InjectMocks
    private HabitTranslationMapper habitTranslationMapper;

    @Test
    void convertTest() {
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();

        HabitTranslation expected = HabitTranslation.builder()
            .description(habitTranslationDto.getDescription())
            .habitItem(habitTranslationDto.getHabitItem())
            .name(habitTranslationDto.getName())
            .build();
        assertEquals(expected, habitTranslationMapper.convert(habitTranslationDto));
    }

    @Test
    void mapAllToListTest() {
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();
        List<HabitTranslationDto> habitTranslationDtoList = List.of(ModelUtils.getHabitTranslationDto());

        HabitTranslation expected = HabitTranslation.builder()
            .description(habitTranslationDto.getDescription())
            .habitItem(habitTranslationDto.getHabitItem())
            .name(habitTranslationDto.getName())
            .build();
        List<HabitTranslation> expectedList = List.of(expected);
        assertEquals(expectedList, habitTranslationMapper.mapAllToList(habitTranslationDtoList));
    }

    @Test
    void mapAllToListWithEnLanguageReturnsListTest() {
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();
        List<HabitTranslationDto> habitTranslationDtoList = List.of(habitTranslationDto);

        HabitTranslation expected = HabitTranslation.builder()
            .description(habitTranslationDto.getDescription())
            .habitItem(habitTranslationDto.getHabitItem())
            .name(habitTranslationDto.getName())
            .build();
        List<HabitTranslation> expectedList = List.of(expected);

        assertEquals(expectedList,
            habitTranslationMapper.mapAllToList(habitTranslationDtoList, AppConstant.DEFAULT_LANGUAGE_CODE));
    }

    @Test
    void mapAllToListWithUaLanguageReturnsListTest() {
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();
        habitTranslationDto.setLanguageCode(AppConstant.LANGUAGE_CODE_UA);
        List<HabitTranslationDto> habitTranslationDtoList = List.of(habitTranslationDto);

        HabitTranslation expected = HabitTranslation.builder()
            .description(habitTranslationDto.getDescription())
            .habitItem(habitTranslationDto.getHabitItem())
            .name(habitTranslationDto.getName())
            .build();
        List<HabitTranslation> expectedList = List.of(expected);

        assertEquals(expectedList,
            habitTranslationMapper.mapAllToList(habitTranslationDtoList, AppConstant.LANGUAGE_CODE_UA));
    }

    @Test
    void mapAllToListWithUaCodeButEmptyUaFieldsReturnListTest() {
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();
        List<HabitTranslationDto> habitTranslationDtoList = List.of(habitTranslationDto);
        habitTranslationDto.setLanguageCode(AppConstant.LANGUAGE_CODE_UA);
        HabitTranslation expected = HabitTranslation.builder()
            .description(habitTranslationDto.getDescription())
            .habitItem(habitTranslationDto.getHabitItem())
            .name(habitTranslationDto.getName())
            .build();
        List<HabitTranslation> expectedList = List.of(expected);

        assertEquals(expectedList,
            habitTranslationMapper.mapAllToList(habitTranslationDtoList, AppConstant.LANGUAGE_CODE_UA));
    }

    @Test
    void mapAllToListWithUkLanguageAndHabitTest() {
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDtoUk();
        habitTranslationDto.setLanguageCode(AppConstant.LANGUAGE_CODE_UA);
        Habit habit = ModelUtils.getHabit();
        Language languageUk = ModelUtils.getLanguageUa();
        List<HabitTranslationDto> habitTranslationDtoList = List.of(habitTranslationDto);
        HabitTranslation expectedHabitTranslation = HabitTranslation.builder()
                .description(habitTranslationDto.getDescription())
                .habitItem(habitTranslationDto.getHabitItem())
                .name(habitTranslationDto.getName())
                .habit(habit)
                .language(languageUk)
                .build();

        List<HabitTranslation> expectedList = List.of(expectedHabitTranslation);

        assertEquals(expectedList,
                habitTranslationMapper.mapAllToList(habitTranslationDtoList, languageUk, habit));
    }

    @Test
    void mapAllToListWithEnLanguageAndHabitTest() {
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();
        habitTranslationDto.setLanguageCode(AppConstant.DEFAULT_LANGUAGE_CODE);
        Habit habit = ModelUtils.getHabit();
        Language languageEn = ModelUtils.getLanguage();
        List<HabitTranslationDto> habitTranslationDtoList = List.of(habitTranslationDto);
        HabitTranslation expectedHabitTranslation = HabitTranslation.builder()
                .description(habitTranslationDto.getDescription())
                .habitItem(habitTranslationDto.getHabitItem())
                .name(habitTranslationDto.getName())
                .habit(habit)
                .language(languageEn)
                .build();

        List<HabitTranslation> expectedList = List.of(expectedHabitTranslation);

        assertEquals(expectedList,
                habitTranslationMapper.mapAllToList(habitTranslationDtoList, languageEn, habit));
    }
}

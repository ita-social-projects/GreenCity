package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HabitTranslationDtoMapperTest {
    @InjectMocks
    private HabitTranslationDtoMapper habitTranslationDtoMapper;

    @Test
    void convertTest() {
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslationForServiceTest();
        habitTranslation.setLanguage(ModelUtils.getLanguage());

        HabitTranslationDto expected = HabitTranslationDto.builder()
            .description(habitTranslation.getDescription())
            .habitItem(habitTranslation.getHabitItem())
            .name(habitTranslation.getName())
            .languageCode("en")
            .build();
        assertEquals(expected, habitTranslationDtoMapper.convert(habitTranslation));
    }

    @Test
    void mapAllToListTest() {
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslationForServiceTest();
        habitTranslation.setLanguage(ModelUtils.getLanguage());

        List<HabitTranslation> habitTranslationList = List.of(habitTranslation);

        HabitTranslationDto expected = HabitTranslationDto.builder()
            .description(habitTranslation.getDescription())
            .habitItem(habitTranslation.getHabitItem())
            .name(habitTranslation.getName())
            .languageCode("en")
            .build();
        List<HabitTranslationDto> expectedList = List.of(expected);
        assertEquals(expectedList, habitTranslationDtoMapper.mapAllToList(habitTranslationList));
    }
}

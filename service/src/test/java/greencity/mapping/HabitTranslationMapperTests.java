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
}

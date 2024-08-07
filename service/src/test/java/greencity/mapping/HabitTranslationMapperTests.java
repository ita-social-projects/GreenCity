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

    @Test
    void mapAllToListTest2() {
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();
        List<HabitTranslationDto> habitTranslationDtoList = List.of(habitTranslationDto);

        HabitTranslation expected = HabitTranslation.builder()
            .description(habitTranslationDto.getDescription())
            .habitItem(habitTranslationDto.getHabitItem())
            .name(habitTranslationDto.getName())
            .build();
        List<HabitTranslation> expectedList = List.of(expected);
        assertEquals(expectedList, habitTranslationMapper.mapAllToList(habitTranslationDtoList, "en"));
    }

    @Test
    void mapAllToListTest3() {
        HabitTranslationDto habitTranslationDtoUa = ModelUtils.getHabitTranslationUaDto();

        List<HabitTranslationDto> habitTranslationDtoListUa = List.of(habitTranslationDtoUa);

        HabitTranslation expectedUa = HabitTranslation.builder()
            .description(habitTranslationDtoUa.getDescriptionUa())
            .habitItem(habitTranslationDtoUa.getHabitItemUa())
            .name(habitTranslationDtoUa.getNameUa())
            .build();
        List<HabitTranslation> expectedUaList = List.of(expectedUa);
        assertEquals(expectedUaList, habitTranslationMapper.mapAllToList(habitTranslationDtoListUa, "ua"));
    }

    @Test
    void convertUaTest() {
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationUaDto();

        HabitTranslation expected = HabitTranslation.builder()
            .description(habitTranslationDto.getDescriptionUa())
            .habitItem(habitTranslationDto.getHabitItemUa())
            .name(habitTranslationDto.getNameUa())
            .build();
        assertEquals(expected, habitTranslationMapper.convertUa(habitTranslationDto));
    }
}

package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.enums.FactOfDayStatus;
import greencity.repository.HabitFactTranslationRepo;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.getFactTranslation;
import static greencity.ModelUtils.getLanguageTranslationDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitFactTranslationServiceImplTest {

    @Mock
    HabitFactTranslationRepo habitFactTranslationRepo;
    @Mock
    HabitFactService habitFactService;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    HabitFactTranslationServiceImpl habitFactTranslationService;

    @Test
    void saveHabitFactAndFactTranslation() {
        HabitFact habitFact = ModelUtils.getHabitFact();
        HabitFactPostDto habitFactPostDto = new HabitFactPostDto();
        HabitIdRequestDto habitIdRequestDto = new HabitIdRequestDto();
        habitIdRequestDto.setId(1L);
        habitFactPostDto.setHabit(habitIdRequestDto);
        habitFactPostDto.setTranslations(Collections.singletonList(ModelUtils.getLanguageTranslationDTO()));
        List<HabitFactTranslation> habitFactTranslations = Collections.singletonList(getFactTranslation());
        when(habitFactService.save(habitFactPostDto)).thenReturn(habitFact);
        when(modelMapper.map(habitFactPostDto.getTranslations(), new TypeToken<List<HabitFactTranslation>>() {
        }.getType())).thenReturn(habitFactTranslations);
        when(habitFactTranslationRepo.saveAll(habitFactTranslations)).thenReturn(habitFactTranslations);
        assertEquals(habitFactTranslations, habitFactTranslationService.saveHabitFactAndFactTranslation(habitFactPostDto));

    }

    @Test
    void saveHabitFactTranslation() {
        List<HabitFactTranslation> habitFactTranslations = Collections.singletonList(getFactTranslation());
        when(habitFactTranslationRepo.saveAll(habitFactTranslations)).thenReturn(habitFactTranslations);

        assertEquals(habitFactTranslations, habitFactTranslationService.saveHabitFactTranslation(habitFactTranslations));
    }

    @Test
    void getHabitFactOfTheDay() {
        List<HabitFactTranslation> list = Collections.singletonList(getFactTranslation());
        when(habitFactTranslationRepo.findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus.CURRENT, 1L))
                .thenReturn(list);
        when(modelMapper.map(list, LanguageTranslationDTO.class)).thenReturn(getLanguageTranslationDTO());
        assertEquals(getLanguageTranslationDTO(), habitFactTranslationService.getHabitFactOfTheDay(1L));
    }
}

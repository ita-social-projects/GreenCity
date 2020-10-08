package greencity.service.impl;

import static greencity.ModelUtils.*;
import greencity.dto.fact.HabitFactPostDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.FactTranslation;
import greencity.entity.HabitFact;
import greencity.entity.enums.FactOfDayStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.FactTranslationRepo;
import greencity.service.HabitFactService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class FactTranslationServiceImplTest {

    @Mock
    private FactTranslationRepo factTranslationRepo;

    @Mock
    private HabitFactService habitFactService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FactTranslationServiceImpl factTranslationService;

    @Test
    void saveHabitFactAndFactTranslation() {
        HabitFact habitFact = getHabitFact();
        HabitFactPostDTO habitFactPostDTO = getHabitFactPostDTO();
        List<FactTranslation> factTranslations = Collections.singletonList(getFactTranslation());
        when(habitFactService.save(habitFactPostDTO)).thenReturn(habitFact);
        when(modelMapper.map(habitFactPostDTO.getTranslations(), new TypeToken<List<FactTranslation>>() {
        }.getType())).thenReturn(factTranslations);
        when(factTranslationRepo.saveAll(factTranslations)).thenReturn(factTranslations);

        factTranslationService.saveHabitFactAndFactTranslation(habitFactPostDTO);
        verify(factTranslationRepo, times(1)).saveAll(factTranslations);
        assertEquals(factTranslations.get(0).getHabitFact(), habitFact);
    }

    @Test
    void saveFactTranslation() {
        List<FactTranslation> factTranslations = Collections.singletonList(getFactTranslation());
        when(factTranslationRepo.saveAll(factTranslations)).thenReturn(factTranslations);
        factTranslationService.saveFactTranslation(factTranslations);
        verify(factTranslationRepo, times(1)).saveAll(factTranslations);
    }

    @Test
    void getFactOfTheDay() {
        List<FactTranslation> list = Collections.singletonList(getFactTranslation());
        when(factTranslationRepo.findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus.CURRENT, 1L))
            .thenReturn(Optional.of(list));
        when(modelMapper.map(list.get(0), LanguageTranslationDTO.class)).thenReturn(getLanguageTranslationDTO());
        assertEquals(getLanguageTranslationDTO(), factTranslationService.getFactOfTheDay(1L));
    }

    @Test
    void getFactOfTheDayFailed() {
        when(factTranslationRepo.findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus.CURRENT, 1L))
            .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> factTranslationService.getFactOfTheDay(1L));
    }
}
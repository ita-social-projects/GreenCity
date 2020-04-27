package greencity.service.impl;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import greencity.entity.FactTranslation;
import greencity.repository.FactTranslationRepo;
import greencity.service.HabitFactService;
import greencity.dto.fact.HabitFactPostDTO;
import greencity.entity.HabitFact;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
}
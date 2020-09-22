package greencity.service.impl;


import greencity.ModelUtils;
import greencity.entity.FactOfTheDayTranslation;
import greencity.repository.FactOfTheDayTranslationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FactOfTheDayTranslationServiceImplTest {

    @Mock
    private FactOfTheDayTranslationRepo factOfTheDayTranslationRepo;
    @InjectMocks
    private FactOfTheDayTranslationServiceImpl factOfTheDayTranslationService;

    @Test
    void getFactOfTheDayById() {
        Long id = 1L;
        FactOfTheDayTranslation factOfTheDayTranslation = new FactOfTheDayTranslation();
        factOfTheDayTranslation.setId(id);
        factOfTheDayTranslation.setContent("Content");
        factOfTheDayTranslation.setLanguage(ModelUtils.getLanguage());
        factOfTheDayTranslation.setFactOfTheDay(ModelUtils.getFactOfTheDay());
        Optional<FactOfTheDayTranslation> fact = Optional.of(factOfTheDayTranslation);
        when(factOfTheDayTranslationRepo.findById(id)).thenReturn(fact);
        assertEquals(fact, factOfTheDayTranslationService.getFactOfTheDayById(id));
    }

    @Test
    void save() {
        Long id = 1L;
        FactOfTheDayTranslation factOfTheDayTranslation = new FactOfTheDayTranslation();
        factOfTheDayTranslation.setId(id);
        factOfTheDayTranslation.setContent("Content");
        factOfTheDayTranslation.setLanguage(ModelUtils.getLanguage());
        factOfTheDayTranslation.setFactOfTheDay(ModelUtils.getFactOfTheDay());
        when(factOfTheDayTranslationRepo.save(factOfTheDayTranslation)).thenReturn(factOfTheDayTranslation);
        assertEquals(factOfTheDayTranslation, factOfTheDayTranslationService.save(factOfTheDayTranslation));
    }

    @Test
    void saveAll() {
        Long id = 1L;
        FactOfTheDayTranslation factOfTheDayTranslation = new FactOfTheDayTranslation();
        factOfTheDayTranslation.setId(id);
        factOfTheDayTranslation.setContent("Content");
        factOfTheDayTranslation.setLanguage(ModelUtils.getLanguage());
        factOfTheDayTranslation.setFactOfTheDay(ModelUtils.getFactOfTheDay());
        List<FactOfTheDayTranslation> factOfTheDayTranslationList = Collections.singletonList(factOfTheDayTranslation);
        when(factOfTheDayTranslationRepo.saveAll(factOfTheDayTranslationList)).thenReturn(factOfTheDayTranslationList);
        assertEquals(factOfTheDayTranslationList, factOfTheDayTranslationService.saveAll(factOfTheDayTranslationList));
    }

    @Test
    void deleteAll() {
        Long id = 1L;
        FactOfTheDayTranslation factOfTheDayTranslation = new FactOfTheDayTranslation();
        factOfTheDayTranslation.setId(id);
        factOfTheDayTranslation.setContent("Content");
        factOfTheDayTranslation.setLanguage(ModelUtils.getLanguage());
        factOfTheDayTranslation.setFactOfTheDay(ModelUtils.getFactOfTheDay());
        List<FactOfTheDayTranslation> factOfTheDayTranslationList = Collections.singletonList(factOfTheDayTranslation);
        factOfTheDayTranslationService.deleteAll(factOfTheDayTranslationList);
        verify(factOfTheDayTranslationRepo).deleteAll(factOfTheDayTranslationList);
    }
}

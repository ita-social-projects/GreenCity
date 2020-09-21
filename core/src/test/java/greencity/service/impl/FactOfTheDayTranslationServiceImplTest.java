package greencity.service.impl;


import greencity.ModelUtils;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.entity.FactOfTheDayTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.FactOfTheDayTranslationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FactOfTheDayTranslationServiceImplTest {

    private FactOfTheDayTranslationServiceImpl factOfTheDayTranslationService;
    @Mock
    private FactOfTheDayTranslationRepo factOfTheDayTranslationRepo;
    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        factOfTheDayTranslationService= new FactOfTheDayTranslationServiceImpl(factOfTheDayTranslationRepo, modelMapper);
    }

    @Test
    void getFactOfTheDayById() {
        Long id = 1L;
        FactOfTheDayTranslation factOfTheDayTranslation = new FactOfTheDayTranslation();
        factOfTheDayTranslation.builder()
                .id(id)
                .content("Content")
                .language(ModelUtils.getLanguage())
                .factOfTheDay(ModelUtils.getFactOfTheDay())
                .build();
        Optional<FactOfTheDayTranslation> fact = Optional.of(factOfTheDayTranslation);
        when(factOfTheDayTranslationRepo.findById(id)).thenReturn(fact);
        assertEquals(fact, factOfTheDayTranslationService.getFactOfTheDayById(id));
    }

    @Test
    void save() {
        FactOfTheDayTranslation factOfTheDayTranslation = new FactOfTheDayTranslation();
        factOfTheDayTranslation.builder()
                .id(1L)
                .content("Content")
                .language(ModelUtils.getLanguage())
                .factOfTheDay(ModelUtils.getFactOfTheDay())
                .build();
        when(factOfTheDayTranslationRepo.save(factOfTheDayTranslation)).thenReturn(factOfTheDayTranslation);
        assertEquals(factOfTheDayTranslation, factOfTheDayTranslationService.save(factOfTheDayTranslation));
    }

    @Test
    void saveAll() {
        FactOfTheDayTranslation factOfTheDayTranslation = new FactOfTheDayTranslation();
        factOfTheDayTranslation.builder()
                .id(1L)
                .content("Content")
                .language(ModelUtils.getLanguage())
                .factOfTheDay(ModelUtils.getFactOfTheDay())
                .build();
        List<FactOfTheDayTranslation> factOfTheDayTranslationList = Collections.singletonList(factOfTheDayTranslation);
        when(factOfTheDayTranslationRepo.saveAll(factOfTheDayTranslationList)).thenReturn(factOfTheDayTranslationList);
        assertEquals(factOfTheDayTranslationList, factOfTheDayTranslationService.saveAll(factOfTheDayTranslationList));
    }

    @Test
    void deleteAll() {
        FactOfTheDayTranslation factOfTheDayTranslation = new FactOfTheDayTranslation();
        factOfTheDayTranslation.builder()
                .id(1L)
                .content("Content")
                .language(ModelUtils.getLanguage())
                .factOfTheDay(ModelUtils.getFactOfTheDay())
                .build();
        List<FactOfTheDayTranslation> factOfTheDayTranslationList = Collections.singletonList(factOfTheDayTranslation);
        factOfTheDayTranslationService.deleteAll(factOfTheDayTranslationList);
        verify(factOfTheDayTranslationRepo).deleteAll(factOfTheDayTranslationList);
    }

    @Test
    void getRandomFactOfTheDayByLanguage() {
        FactOfTheDayTranslation factOfTheDayTranslation = new FactOfTheDayTranslation();
        factOfTheDayTranslation.builder()
                .id(1L)
                .content("Content")
                .language(ModelUtils.getLanguage())
                .factOfTheDay(ModelUtils.getFactOfTheDay())
                .build();
        String languageCode = "uk";
        FactOfTheDayTranslationDTO factOfTheDayTranslationDTO = new FactOfTheDayTranslationDTO();
        factOfTheDayTranslationDTO.setId(1L);
        factOfTheDayTranslationDTO.setContent("Content");
        when(factOfTheDayTranslationRepo.getRandomFactOfTheDayTranslation(languageCode)).thenReturn(Optional.of(factOfTheDayTranslation));
        when(modelMapper.map(factOfTheDayTranslation, FactOfTheDayTranslationDTO.class)).thenReturn(factOfTheDayTranslationDTO);
        assertEquals(factOfTheDayTranslationDTO, factOfTheDayTranslationService.getRandomFactOfTheDayByLanguage(languageCode));
    }

    @Test
    void getRandomFactOfTheDayByLanguageBadRequest() {
        FactOfTheDayTranslation factOfTheDayTranslation = new FactOfTheDayTranslation();
        factOfTheDayTranslation.builder()
                .id(1L)
                .content("Content")
                .language(ModelUtils.getLanguage())
                .factOfTheDay(ModelUtils.getFactOfTheDay())
                .build();
        String languageCode = "uk";
        FactOfTheDayTranslationDTO factOfTheDayTranslationDTO = new FactOfTheDayTranslationDTO();
        factOfTheDayTranslationDTO.setId(1L);
        factOfTheDayTranslationDTO.setContent("Content");
        when(factOfTheDayTranslationRepo.getRandomFactOfTheDayTranslation(languageCode)).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () ->
                factOfTheDayTranslationService.getRandomFactOfTheDayByLanguage(languageCode)
        );
    }
}

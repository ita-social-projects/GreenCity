/*
package greencity.service.impl;

import greencity.dto.advice.AdviceDTO;
import greencity.dto.habitfact.HabitFactDTO;
import greencity.dto.habitfact.HabitFactPostDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitDictionaryIdDto;
import greencity.entity.FactTranslation;
import greencity.entity.HabitDictionary;
import greencity.entity.HabitFact;
import static greencity.enums.FactOfDayStatus.CURRENT;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.FactTranslationRepo;
import greencity.repository.HabitDictionaryRepo;
import greencity.repository.HabitFactRepo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

@ExtendWith(MockitoExtension.class)
class HabitFactServiceImplTest {

    @InjectMocks
    private HabitFactServiceImpl habitFactService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FactTranslationRepo factTranslationRepo;

    @Mock
    private HabitFactRepo habitFactRepo;

    @Mock
    private HabitDictionaryRepo habitDictionaryRepo;

    private HabitDictionary habitDictionary = new HabitDictionary(1L, "test", null, null);
    private HabitFact habitFact = new HabitFact(1L, null, habitDictionary);
    private FactTranslation factTranslation = new FactTranslation(1L, null, CURRENT, null, "test");
    private HabitFactDTO habitFactDTO = new HabitFactDTO(1L, "habitfact", null);
    private LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO(null, "test");
    private HabitFactPostDTO habitFactPostDTO = new HabitFactPostDTO(null, new HabitDictionaryIdDto(1L));

    @Test
     void getAllHabitFactsFailed() {
        List<AdviceDTO> expected = Collections.emptyList();
        when(modelMapper.map(factTranslationRepo.findAll(), new TypeToken<List<LanguageTranslationDTO>>() {
        }.getType())).thenReturn(expected);
        assertEquals(expected, habitFactService.getAllHabitFacts());
    }

    @Test
     void getRandomHabitFactByHabitId() {
        when(factTranslationRepo.getRandomFactTranslationByHabitIdAndLanguage("en", 1L))
            .thenReturn(Optional.of(factTranslation));
        when(modelMapper.map(factTranslation, LanguageTranslationDTO.class)).thenReturn(languageTranslationDTO);
        assertEquals(languageTranslationDTO, habitFactService.getRandomHabitFactByHabitIdAndLanguage(1L, "en"));
    }

    @Test
     void getRandomHabitFactByHabitIdFailed() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> habitFactService.getRandomHabitFactByHabitIdAndLanguage(1L, "en"));
    }

    @Test
     void getHabitFactById() {
        when(habitFactRepo.findById(anyLong())).thenReturn(Optional.of(habitFact));
        when(modelMapper.map(habitFact, HabitFactDTO.class)).thenReturn(habitFactDTO);
        assertEquals(habitFactDTO, habitFactService.getHabitFactById(anyLong()));
    }

    @Test
     void getHabitFactByIdFailed() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> habitFactService.getHabitFactById(1L));
    }

    @Test
     void getHabitFactByName() {
        when(factTranslationRepo.findFactTranslationByLanguageCodeAndHabitFact("en", "test"))
            .thenReturn(Optional.of(factTranslation));
        when(modelMapper.map(factTranslation, HabitFactDTO.class)).thenReturn(habitFactDTO);
        assertEquals(habitFactDTO, habitFactService.getHabitFactByName("en", "test"));
    }

    @Test
     void getHabitFactByNameFailed() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> habitFactService.getHabitFactByName("en", "test"));
    }

    @Test
     void save() {
        when(habitFactService.save(habitFactPostDTO)).thenReturn(habitFact);
        assertEquals(habitFact, habitFactService.save(habitFactPostDTO));
    }

    @Test
     void update() {
        when(habitDictionaryRepo.findById(anyLong())).thenReturn(Optional.of(habitDictionary));
        when(habitFactRepo.findById(anyLong())).thenReturn(Optional.of(habitFact));
        when(habitFactRepo.save(habitFact)).thenReturn(habitFact);
        when(habitFactService.update(habitFactPostDTO, 1L)).thenReturn(habitFact);
        assertEquals(habitFact, habitFactService.update(habitFactPostDTO, 1L));
    }

    @Test
     void updateFailed() {
        Assertions
            .assertThrows(NotUpdatedException.class,
                () -> habitFactService.update(habitFactPostDTO, 1L));
    }

    @Test
     void delete() {
        when(habitFactRepo.findById(habitFact.getId())).thenReturn(Optional.of(habitFact));
        assertEquals(habitFact.getId(), habitFactService.delete(habitFact.getId()));
        verify(habitFactRepo, times(1)).deleteById(anyLong());
    }

    @Test
     void deleteFailed() {
        Assertions
            .assertThrows(NotDeletedException.class,
                () -> habitFactService.delete(1L));
    }

}*/

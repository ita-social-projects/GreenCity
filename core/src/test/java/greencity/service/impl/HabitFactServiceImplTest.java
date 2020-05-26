package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static greencity.entity.enums.FactOfDayStatus.CURRENT;

import greencity.dto.advice.AdviceDTO;
import greencity.dto.fact.HabitFactDTO;
import greencity.dto.fact.HabitFactPostDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitDictionaryIdDto;
import greencity.entity.FactTranslation;
import greencity.entity.HabitDictionary;
import greencity.entity.HabitFact;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.FactTranslationRepo;
import greencity.repository.HabitDictionaryRepo;
import greencity.repository.HabitFactRepo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

@RunWith(MockitoJUnitRunner.class)
public class HabitFactServiceImplTest {

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
    private FactTranslation factTranslation = new FactTranslation(1L, null,CURRENT, null, "test");
    private HabitFactDTO habitFactDTO = new HabitFactDTO(1L, "fact", null);
    private LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO(null, "test");
    private HabitFactPostDTO habitFactPostDTO = new HabitFactPostDTO(null, new HabitDictionaryIdDto(1L));

    @Test
    public void getAllHabitFactsFailed() {
        List<AdviceDTO> expected = Collections.emptyList();
        when(modelMapper.map(factTranslationRepo.findAll(), new TypeToken<List<LanguageTranslationDTO>>() {
        }.getType())).thenReturn(expected);
        assertEquals(expected, habitFactService.getAllHabitFacts());
    }

    @Test
    public void getRandomHabitFactByHabitId() {
        when(factTranslationRepo.getRandomFactTranslationByHabitIdAndLanguage("en", 1L)).thenReturn(Optional.of(factTranslation));
        when(modelMapper.map(factTranslation, LanguageTranslationDTO.class)).thenReturn(languageTranslationDTO);
        assertEquals(languageTranslationDTO, habitFactService.getRandomHabitFactByHabitIdAndLanguage(1L, "en"));
    }

    @Test(expected = NotFoundException.class)
    public void getRandomHabitFactByHabitIdFailed() {
        habitFactService.getRandomHabitFactByHabitIdAndLanguage(1L, "en");
    }

    @Test
    public void getHabitFactById() {
        when(habitFactRepo.findById(anyLong())).thenReturn(Optional.of(habitFact));
        when(modelMapper.map(habitFact, HabitFactDTO.class)).thenReturn(habitFactDTO);
        assertEquals(habitFactDTO, habitFactService.getHabitFactById(anyLong()));
    }

    @Test(expected = NotFoundException.class)
    public void getHabitFactByIdFailed() {
        habitFactService.getHabitFactById(anyLong());
    }

    @Test
    public void getHabitFactByName() {
        when(factTranslationRepo.findFactTranslationByLanguage_CodeAndHabitFact("en", "test")).thenReturn(Optional.of(factTranslation));
        when(modelMapper.map(factTranslation, HabitFactDTO.class)).thenReturn(habitFactDTO);
        assertEquals(habitFactDTO, habitFactService.getHabitFactByName("en", "test"));
    }

    @Test(expected = NotFoundException.class)
    public void getHabitFactByNameFailed() {
        habitFactService.getHabitFactByName("en", "test");
    }

    @Test
    public void save() {
        when(habitFactService.save(habitFactPostDTO)).thenReturn(habitFact);
        assertEquals(habitFact, habitFactService.save(habitFactPostDTO));
    }

    @Test
    public void update() {
        when(habitDictionaryRepo.findById(anyLong())).thenReturn(Optional.of(habitDictionary));
        when(habitFactRepo.findById(anyLong())).thenReturn(Optional.of(habitFact));
        when(habitFactRepo.save(habitFact)).thenReturn(habitFact);
        when(habitFactService.update(habitFactPostDTO, 1L)).thenReturn(habitFact);
        assertEquals(habitFact, habitFactService.update(habitFactPostDTO, 1L));
    }

    @Test(expected = NotUpdatedException.class)
    public void updateFailed() {
        habitFactService.update(habitFactPostDTO, 1L);
    }

    @Test
    public void delete() {
        when(habitFactRepo.findById(habitFact.getId())).thenReturn(Optional.of(habitFact));
        assertEquals(habitFact.getId(), habitFactService.delete(habitFact.getId()));
        verify(habitFactRepo, times(1)).deleteById(anyLong());
    }

    @Test(expected = NotDeletedException.class)
    public void deleteFailed() {
        habitFactService.delete(habitFact.getId());
    }

}
package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import greencity.dto.advice.AdviceDTO;
import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitDictionaryIdDto;
import greencity.entity.Advice;
import greencity.entity.AdviceTranslation;
import greencity.entity.HabitDictionary;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AdviceRepo;
import greencity.repository.AdviceTranslationRepo;
import greencity.repository.HabitDictionaryRepo;
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
public class AdviceServiceImplTest {

    @InjectMocks
    private AdviceServiceImpl adviceService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AdviceRepo adviceRepo;

    @Mock
    private AdviceTranslationRepo adviceTranslationRepo;

    @Mock
    private HabitDictionaryRepo habitDictionaryRepo;

    private AdviceDTO adviceDTO = new AdviceDTO(1L, "content", null);
    private LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO(null, "test");
    private AdvicePostDTO advicePostDTO = new AdvicePostDTO(null, new HabitDictionaryIdDto(1L));
    private HabitDictionary habitDictionary = new HabitDictionary(1L, "test", null, null);
    private Advice advice = new Advice(1L, Collections.emptyList(), habitDictionary);
    private AdviceTranslation adviceTranslation = new AdviceTranslation(1L, null, advice, "test");


    @Test
    public void getAllAdvicesFailed() {
        List<LanguageTranslationDTO> expected = Collections.emptyList();
        when(modelMapper.map(adviceTranslationRepo.findAll(), new TypeToken<List<LanguageTranslationDTO>>() {
        }.getType())).thenReturn(expected);
        assertEquals(expected, adviceService.getAllAdvices());
    }

    @Test
    public void getRandomAdviceByHabitIdAndLanguage() {
        when(adviceTranslationRepo.getRandomAdviceTranslationByHabitIdAndLanguage("en", 1L))
            .thenReturn(Optional.of(adviceTranslation));
        when(modelMapper.map(adviceTranslation, LanguageTranslationDTO.class)).thenReturn(languageTranslationDTO);
        assertEquals(languageTranslationDTO, adviceService.getRandomAdviceByHabitIdAndLanguage(1L, "en"));
    }

    @Test(expected = NotFoundException.class)
    public void getRandomAdviceByHabitIdFailed() {
        adviceService.getRandomAdviceByHabitIdAndLanguage(1L, "en");
    }

    @Test
    public void getAdviceById() {
        when(adviceRepo.findById(anyLong())).thenReturn(Optional.of(advice));
        when(modelMapper.map(advice, AdviceDTO.class)).thenReturn(adviceDTO);
        assertEquals(adviceDTO, adviceService.getAdviceById(anyLong()));
    }

    @Test(expected = NotFoundException.class)
    public void getAdviceByIdFailed() {
        adviceService.getAdviceById(anyLong());
    }

    @Test
    public void getAdviceByName() {
        when(adviceTranslationRepo.findAdviceTranslationByLanguage_CodeAndAdvice("en", "test")).thenReturn(Optional.of(adviceTranslation));
        when(modelMapper.map(adviceTranslation, AdviceDTO.class)).thenReturn(adviceDTO);
        assertEquals(adviceDTO, adviceService.getAdviceByName("en", "test"));
    }

    @Test(expected = NotFoundException.class)
    public void getAdviceByNameFailed() {
        adviceService.getAdviceByName("en", "test");
    }

    @Test
    public void save() {
        when(adviceService.save(advicePostDTO)).thenReturn(advice);
        assertEquals(advice, adviceService.save(advicePostDTO));
    }

    @Test
    public void update() {
        when(habitDictionaryRepo.findById(anyLong())).thenReturn(Optional.of(habitDictionary));
        when(adviceRepo.findById(anyLong())).thenReturn(Optional.of(advice));
        when(adviceRepo.save(advice)).thenReturn(advice);
        when(adviceService.update(advicePostDTO, 1L)).thenReturn(advice);
        assertEquals(advice, adviceService.update(advicePostDTO, 1L));
    }

    @Test(expected = NotUpdatedException.class)
    public void updateFailed() {
        adviceService.update(advicePostDTO, 1L);

    }

    @Test
    public void delete() {
        when(adviceRepo.findById(advice.getId())).thenReturn(Optional.of(advice));
        assertEquals(advice.getId(), adviceService.delete(advice.getId()));
        verify(adviceRepo, times(1)).deleteById(anyLong());
    }

    @Test(expected = NotDeletedException.class)
    public void deleteFailed() {
        adviceService.delete(advice.getId());
    }
}

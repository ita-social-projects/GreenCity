package greencity.service.impl;

import greencity.dto.advice.AdviceDTO;
import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitDictionaryIdDto;
import greencity.entity.Advice;
import greencity.entity.HabitDictionary;
import greencity.entity.localization.AdviceTranslation;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AdviceRepo;
import greencity.repository.AdviceTranslationRepo;
import greencity.repository.HabitDictionaryRepo;
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
import org.springframework.dao.EmptyResultDataAccessException;

@ExtendWith(MockitoExtension.class)
class AdviceServiceImplTest {
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
    void getAllAdvices() {
        List<LanguageTranslationDTO> expected = Collections.emptyList();
        when(modelMapper.map(adviceTranslationRepo.findAll(), new TypeToken<List<LanguageTranslationDTO>>() {
        }.getType())).thenReturn(expected);
        assertEquals(expected, adviceService.getAllAdvices());
    }

    @Test
    void getRandomAdviceByHabitIdAndLanguage() {
        when(adviceTranslationRepo.getRandomAdviceTranslationByHabitIdAndLanguage("en", 1L))
            .thenReturn(Optional.of(adviceTranslation));
        when(modelMapper.map(adviceTranslation, LanguageTranslationDTO.class)).thenReturn(languageTranslationDTO);
        assertEquals(languageTranslationDTO, adviceService.getRandomAdviceByHabitIdAndLanguage(1L, "en"));
    }

    @Test
    void getRandomAdviceByHabitIdFailed() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> adviceService.getRandomAdviceByHabitIdAndLanguage(1L, "en"));
    }

    @Test
    void getAdviceById() {
        when(adviceRepo.findById(anyLong())).thenReturn(Optional.of(advice));
        when(modelMapper.map(advice, AdviceDTO.class)).thenReturn(adviceDTO);
        assertEquals(adviceDTO, adviceService.getAdviceById(anyLong()));
    }

    @Test
    void getAdviceByIdFailed() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> adviceService.getAdviceById(1L));
    }

    @Test
    void getAdviceByName() {
        when(adviceTranslationRepo.findAdviceTranslationByLanguage_CodeAndAdvice("en", "test"))
            .thenReturn(Optional.of(adviceTranslation));
        when(modelMapper.map(adviceTranslation, AdviceDTO.class)).thenReturn(adviceDTO);
        assertEquals(adviceDTO, adviceService.getAdviceByName("en", "test"));
    }

    @Test
    void getAdviceByNameFailed() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> adviceService.getAdviceByName("en", "test"));
    }

    @Test
    void save() {
        when(adviceService.save(advicePostDTO)).thenReturn(advice);
        assertEquals(advice, adviceService.save(advicePostDTO));
    }

    @Test
    void update() {
        when(habitDictionaryRepo.findById(anyLong())).thenReturn(Optional.of(habitDictionary));
        when(adviceRepo.findById(anyLong())).thenReturn(Optional.of(advice));
        when(adviceRepo.save(advice)).thenReturn(advice);
        when(adviceService.update(advicePostDTO, 1L)).thenReturn(advice);
        assertEquals(advice, adviceService.update(advicePostDTO, 1L));
    }

    @Test
    void updateFailed() {
        Assertions
            .assertThrows(NotUpdatedException.class,
                () -> adviceService.update(advicePostDTO, 1L));
    }

    @Test
    void delete() {
        assertEquals(advice.getId(), adviceService.delete(advice.getId()));
        verify(adviceRepo, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteFailed() {
        doThrow(new EmptyResultDataAccessException(1)).when(adviceRepo).deleteById(advice.getId());
        Long id = advice.getId();
        Assertions
            .assertThrows(NotDeletedException.class,
                () -> adviceService.delete(id));
    }
}

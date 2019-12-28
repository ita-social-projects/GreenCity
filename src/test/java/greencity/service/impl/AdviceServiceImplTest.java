package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import greencity.dto.advice.AdviceDTO;
import greencity.entity.Advice;
import greencity.entity.HabitDictionary;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AdviceRepo;
import greencity.repository.AdviceTranslationRepo;
import greencity.repository.HabitDictionaryRepo;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

@RunWith(MockitoJUnitRunner.class)
public class AdviceServiceImplTest {
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AdviceServiceImpl adviceService;

    @Mock
    private AdviceRepo adviceRepo;

    @Mock
    private AdviceTranslationRepo adviceTranslationRepo;

    @Mock
    private HabitDictionaryRepo habitDictionaryRepo;

    private AdviceDTO adviceDTO = new AdviceDTO(1L, "content", null);
    //    private AdvicePostDTO advicePostDTO = new AdvicePostDTO("content", new HabitDictionaryIdDto(1L));
    private HabitDictionary habitDictionary = new HabitDictionary(1L, "test", "test", "test", null);

    private Advice advice = new Advice(1L, Collections.emptyList(), habitDictionary);
    //  private Language language = new Language(1L, "en", anyList());

    //  private AdviceTranslation adviceTranslation = new AdviceTranslation(1L, language, content, "djdj");


//    @Test
//    public void getAllAdvicesFailed() {
//        List<AdviceDTO> expected = Collections.emptyList();
//        when(modelMapper.map(adviceRepo.findAll(), new TypeToken<List<AdviceDTO>>() {
//        }.getType())).thenReturn(expected);
//        assertEquals(expected, adviceService.getAllAdvices());
//    }

//    @Test
//    public void getRandomAdviceByHabitIdAndLanguage() {
//        when(adviceTranslationRepo.getRandomAdviceTranslationByHabitIdAndLanguage(anyString(), anyLong()))
//                .thenReturn(Optional.of(adviceTranslation));
//        when(modelMapper.map(content, AdviceDTO.class)).thenReturn(adviceDTO);
//        assertEquals(adviceDTO, adviceService.getRandomAdviceByHabitIdAndLanguage(anyLong(), "en"));
//    }

//    @Test(expected = NotFoundException.class)
//    public void getRandomAdviceByHabitIdFailed() {
//        adviceService.getRandomAdviceByHabitIdAndLanguage(anyLong());
//    }

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

//    @Test
//    public void getAdviceByName() {
//        when(adviceRepo.findAdviceByAdvice(anyString())).thenReturn(Optional.of(content));
//        when(modelMapper.map(content, AdviceDTO.class)).thenReturn(adviceDTO);
//        assertEquals(adviceDTO, adviceService.getAdviceByName(anyString()));
//    }

//    @Test(expected = NotFoundException.class)
//    public void getAdviceByNameFailed() {
//        adviceService.getAdviceByName(anyString());
//    }

//    @Test
//    public void saveAdviceAndAdviceTranslation() {
//        when(adviceService.saveAdviceAndAdviceTranslation(advicePostDTO)).thenReturn(content);
//        assertEquals(content, adviceService.saveAdviceAndAdviceTranslation(advicePostDTO));
//    }
//
//    @Test(expected = NotSavedException.class)
//    public void saveFailed() {
//        when(adviceRepo.findAdviceByAdvice(anyString())).thenReturn(Optional.of(content));
//        adviceService.saveAdviceAndAdviceTranslation(advicePostDTO);
//    }
//
//    @Test
//    public void update() {
//        when(habitDictionaryRepo.findById(anyLong())).thenReturn(Optional.of(habitDictionary));
//        when(adviceRepo.findById(anyLong())).thenReturn(Optional.of(content));
//        when(adviceRepo.saveAdviceAndAdviceTranslation(content)).thenReturn(content);
//        when(adviceService.update(advicePostDTO, 1L)).thenReturn(content);
//        assertEquals(content, adviceService.update(advicePostDTO, 1L));
//    }
//
//    @Test(expected = NotUpdatedException.class)
//    public void updateFailed() {
//        adviceService.update(advicePostDTO, 1L);
//
//    }

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

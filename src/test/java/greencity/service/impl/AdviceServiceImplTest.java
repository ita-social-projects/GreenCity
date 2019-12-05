package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import greencity.dto.advice.AdviceDTO;
import greencity.dto.advice.AdvicePostDTO;
import greencity.dto.user.HabitDictionaryIdDto;
import greencity.entity.Advice;
import greencity.entity.HabitDictionary;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AdviceRepo;
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

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AdviceServiceImpl adviceService;

    @Mock
    private AdviceRepo adviceRepo;

    @Mock
    private HabitDictionaryRepo habitDictionaryRepo;

    private AdviceDTO adviceDTO = new AdviceDTO(1L, "advice", null);
    private AdvicePostDTO advicePostDTO = new AdvicePostDTO("advice", new HabitDictionaryIdDto(1L));
    private HabitDictionary habitDictionary = new HabitDictionary(1L, "test", "test", null);
    private Advice advice = new Advice(1L, "advice", habitDictionary);

    @Test
    public void getAllAdvicesFailed() {
        List<AdviceDTO> expected = Collections.emptyList();
        when(modelMapper.map(adviceRepo.findAll(), new TypeToken<List<AdviceDTO>>() {
        }.getType())).thenReturn(expected);
        assertEquals(expected, adviceService.getAllAdvices());
    }

    @Test
    public void getRandomAdviceByHabitId() {
        when(adviceRepo.getRandomAdviceByHabitId(anyLong())).thenReturn(Optional.of(advice));
        when(modelMapper.map(advice, AdviceDTO.class)).thenReturn(adviceDTO);
        assertEquals(adviceDTO, adviceService.getRandomAdviceByHabitId(anyLong()));
    }

    @Test(expected = NotFoundException.class)
    public void getRandomAdviceByHabitIdFailed() {
        adviceService.getRandomAdviceByHabitId(anyLong());
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
        when(adviceRepo.findAdviceByAdvice(anyString())).thenReturn(Optional.of(advice));
        when(modelMapper.map(advice, AdviceDTO.class)).thenReturn(adviceDTO);
        assertEquals(adviceDTO, adviceService.getAdviceByName(anyString()));
    }

    @Test(expected = NotFoundException.class)
    public void getAdviceByNameFailed() {
        adviceService.getAdviceByName(anyString());
    }

    @Test
    public void save() {
        when(adviceService.save(advicePostDTO)).thenReturn(advice);
        assertEquals(advice, adviceService.save(advicePostDTO));
    }

    @Test(expected = NotSavedException.class)
    public void saveFailed() {
        when(adviceRepo.findAdviceByAdvice(anyString())).thenReturn(Optional.of(advice));
        adviceService.save(advicePostDTO);
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
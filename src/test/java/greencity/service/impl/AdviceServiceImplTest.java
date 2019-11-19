package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import greencity.dto.advice.AdviceAdminDTO;
import greencity.dto.advice.AdvicePostDTO;
import greencity.entity.Advice;
import greencity.entity.HabitDictionary;
import greencity.exception.NotDeletedException;
import greencity.exception.NotFoundException;
import greencity.exception.NotSavedException;
import greencity.exception.NotUpdatedException;
import greencity.repository.AdviceRepo;
import greencity.repository.HabitDictionaryRepo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AdviceServiceImplTest {

    @InjectMocks
    private AdviceServiceImpl adviceService;

    @Mock
    private AdviceRepo adviceRepo;

    @Mock
    private HabitDictionaryRepo habitDictionaryRepo;

    private HabitDictionary habitDictionary = new HabitDictionary(1L, "test", null);
    private Advice advice = new Advice(4L, "test", habitDictionary);

    @Test
    public void getAllAdvicesFailed() {
        List<AdviceAdminDTO> expected = Collections.emptyList();
        when(adviceRepo.findAll().stream().map(AdviceAdminDTO::new).collect(Collectors.toList())).thenReturn(expected);
        assertEquals(expected, adviceService.getAllAdvices());
    }

    @Test
    public void getRandomAdviceByHabitId() {
        AdviceAdminDTO expected = new AdviceAdminDTO(advice);
        when(adviceRepo.getRandomAdviceByHabitId(anyLong())).thenReturn(Optional.of(advice));
        assertEquals(expected, adviceService.getRandomAdviceByHabitId(anyLong()));
    }

    @Test(expected = NotFoundException.class)
    public void getRandomAdviceByHabitIdFailed() {
        adviceService.getRandomAdviceByHabitId(anyLong());
    }

    @Test
    public void getAdviceById() {
        AdviceAdminDTO expected = new AdviceAdminDTO(advice);
        when(adviceRepo.findById(anyLong())).thenReturn(Optional.of(advice));
        assertEquals(expected, adviceService.getAdviceById(anyLong()));
    }

    @Test(expected = NotFoundException.class)
    public void getAdviceByIdFailed() {
        adviceService.getAdviceById(anyLong());
    }

    @Test
    public void getAdviceByName() {
        AdviceAdminDTO expected = new AdviceAdminDTO(advice);
        when(adviceRepo.findAdviceByName(anyString())).thenReturn(Optional.of(advice));
        assertEquals(expected, adviceService.getAdviceByName(anyString()));
    }

    @Test(expected = NotFoundException.class)
    public void getAdviceByNameFailed() {
        adviceService.getAdviceByName(anyString());
    }

    @Test
    public void save() {
        AdvicePostDTO advicePostDTO = new AdvicePostDTO(advice);
        when(habitDictionaryRepo.findById(anyLong())).thenReturn(Optional.of(habitDictionary));
        when(adviceService.save(advicePostDTO)).thenReturn(advice);
        assertEquals(advice, adviceService.save(advicePostDTO));
    }

    @Test(expected = NotSavedException.class)
    public void saveFailed() {
        AdvicePostDTO advicePostDTO = new AdvicePostDTO(advice);
        adviceService.save(advicePostDTO);
    }

    @Test
    public void update() {
        AdvicePostDTO adviceAdminDTO = new AdvicePostDTO(advice);
        when(habitDictionaryRepo.findById(anyLong())).thenReturn(Optional.of(habitDictionary));
        when(adviceRepo.findById(anyLong())).thenReturn(Optional.of(advice));
        when(adviceRepo.save(advice)).thenReturn(advice);
        when(adviceService.update(adviceAdminDTO, 1L)).thenReturn(advice);
        assertEquals(advice, adviceService.update(adviceAdminDTO, 1L));
    }

    @Test(expected = NotUpdatedException.class)
    public void updateFailed() {
        AdvicePostDTO adviceAdminDTO = new AdvicePostDTO(advice);
        adviceService.update(adviceAdminDTO, 1L);

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
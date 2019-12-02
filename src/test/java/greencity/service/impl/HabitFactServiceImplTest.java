package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import greencity.dto.fact.HabitFactDTO;
import greencity.entity.HabitDictionary;
import greencity.entity.HabitFact;
import greencity.repository.HabitFactRepo;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HabitFactServiceImplTest {

    @InjectMocks
    private HabitFactServiceImpl habitFactService;

    @Mock
    private HabitFactRepo habitFactRepo;

    private HabitDictionary habitDictionary = new HabitDictionary(1L, "test", "test", null);
    private HabitFact habitFact = new HabitFact(1L, "test", habitDictionary);

    @Test
    public void getRandomHabitFactByHabitId() {
        HabitFactDTO expected = new HabitFactDTO(habitFact);
        when(habitFactRepo.getRandomHabitFactByHabitId(anyLong())).thenReturn(Optional.of(habitFact));
        assertEquals(expected, habitFactService.getRandomHabitFactByHabitId(anyLong()));
    }
}
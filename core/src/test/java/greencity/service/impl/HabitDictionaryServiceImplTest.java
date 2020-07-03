package greencity.service.impl;

import greencity.entity.HabitDictionary;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitDictionaryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static greencity.ModelUtils.getHabitDictionary;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class HabitDictionaryServiceImplTest {
    @InjectMocks
    private HabitDictionaryServiceImpl habitDictionaryService;
    @Mock
    private HabitDictionaryRepo habitDictionaryRepo;

    @Test
    void findByIdTest() {
        HabitDictionary expected = getHabitDictionary();
        when(habitDictionaryRepo.findById(expected.getId())).thenReturn(Optional.of(expected));
        habitDictionaryService.findById(1L);
        verify(habitDictionaryRepo, times(1)).findById(anyLong());
    }

    @Test
    void findByIdNotFoundExceptionTest() {
        assertThrows(NotFoundException.class, () -> {
            habitDictionaryService.findById(getHabitDictionary().getId());
        });
    }
}

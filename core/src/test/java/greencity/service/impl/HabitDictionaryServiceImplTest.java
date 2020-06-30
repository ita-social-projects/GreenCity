package greencity.service.impl;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.entity.HabitDictionary;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitDictionaryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class HabitDictionaryServiceImplTest {
    @Mock
    private HabitDictionaryRepo habitDictionaryRepo;

    @Test
    public void findByIdTest() {
        HabitDictionary expected = ModelUtils.getHabitDictionary();
        when(habitDictionaryRepo.findById(expected.getId())).thenReturn(Optional.of(expected));
        assertEquals(Optional.of(ModelUtils.getHabitDictionary()), habitDictionaryRepo.findById(1L));
    }

    @Test
    public void findByIdNotFoundExceptionTest() {
        when(habitDictionaryRepo.findById(1L))
                .thenThrow(new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + 1L));
        assertThrows(NotFoundException.class, () -> {
            habitDictionaryRepo.findById(1L);
        });
    }
}

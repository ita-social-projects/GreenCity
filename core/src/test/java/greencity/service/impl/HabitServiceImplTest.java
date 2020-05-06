package greencity.service.impl;

import greencity.ModelUtils;
import greencity.entity.Habit;
import greencity.entity.HabitDictionary;
import greencity.entity.HabitDictionaryTranslation;
import greencity.repository.HabitDictionaryTranslationRepo;
import greencity.repository.HabitRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class HabitServiceImplTest {

    @InjectMocks
    private HabitServiceImpl habitService;

    @Mock
    private HabitDictionaryTranslationRepo habitDictionaryTranslationRepo;

    @Mock
    private HabitRepo habitRepo;

    private Habit habit = ModelUtils.getHabit();
    private HabitDictionaryTranslation habitDictionaryTranslation = ModelUtils.getHabitDictionaryTranslation();

    @Test
    void getHabitDictionaryTranslation() {
        when(habitDictionaryTranslationRepo.findByHabitDictionaryAndLanguageCode(any(HabitDictionary.class), anyString()))
                .thenReturn(Optional.of(habitDictionaryTranslation));
        assertEquals(habitDictionaryTranslation, habitService.getHabitDictionaryTranslation(habit, "en"));
    }

    @Test
    void getById() {
        when(habitRepo.findById(anyLong())).thenReturn(Optional.of(habit));
        assertEquals(habit, habitService.getById(1L));
    }
}
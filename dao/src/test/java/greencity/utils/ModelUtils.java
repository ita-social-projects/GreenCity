package greencity.utils;

import greencity.entity.*;
import greencity.entity.localization.AdviceTranslation;
import greencity.enums.FactOfDayStatus;

import java.util.Collections;
import java.util.List;

public class ModelUtils {
    public static Advice getAdvice() {
        return Advice.builder().id(1L)
            .habit(Habit.builder().id(1L).image("image_one.png")
                .build())
            .build();
    }

    public static AdviceTranslation getAdviceTranslationFirst() {
        return AdviceTranslation.builder()
            .id(1L)
            .language(Language.builder().id(1L).code("uk").build())
            .content("Привіт")
            .advice(getAdvice()).build();
    }

    public static AdviceTranslation getAdviceTranslationSecond() {
        return AdviceTranslation.builder()
            .id(2L)
            .language(Language.builder().id(2L).code("en").build())
            .content("Hello")
            .advice(Advice.builder().id(2L)
                .habit(Habit.builder().id(2L).image("image_two.png")
                    .build())
                .build())
            .build();
    }

    public static AdviceTranslation getAdviceTranslationThird() {
        return AdviceTranslation.builder()
            .id(3L)
            .language(Language.builder().id(3L).code("ru").build())
            .content("Привет")
            .advice(Advice.builder().id(3L)
                .habit(Habit.builder().id(3L).image("image_three.png")
                    .build())
                .build())
            .build();
    }

    public static List<AdviceTranslation> getAdviceTranslations() {
        return List.of(getAdviceTranslationFirst(), getAdviceTranslationSecond(), getAdviceTranslationThird());
    }

    public static HabitFactTranslation getHabitFactTranslation() {
        return HabitFactTranslation.builder()
            .id(1L)
            .content("content")
            .language(getLanguage())
            .factOfDayStatus(FactOfDayStatus.POTENTIAL)
            .habitFact(null)
            .build();
    }

    public static HabitFact getHabitFact() {
        return new HabitFact(1L, Collections.singletonList(getHabitFactTranslation()), null);
    }

    public static Language getLanguage() {
        return new Language(1L, "en", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }
}

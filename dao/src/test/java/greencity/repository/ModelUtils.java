package greencity.repository;

import greencity.entity.Advice;
import greencity.entity.Habit;
import greencity.entity.Language;
import greencity.entity.localization.AdviceTranslation;

import java.util.ArrayList;
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

    public static List<Advice> getAdvices() {
        return new ArrayList<>(List.of(Advice.builder().id(1L)
                .habit(Habit.builder().id(2L).image("image_one.png")
                        .build()).translations(List.of(getAdviceTranslationFirst()))
                .build(), Advice.builder().id(2L)
                .habit(Habit.builder().id(2L).image("image_one.png")
                        .build()).translations(List.of(getAdviceTranslationSecond()))
                .build(), Advice.builder().id(3L)
                .habit(Habit.builder().id(3L).image("image_one.png")
                        .build()).translations(List.of(getAdviceTranslationThird()))
                .build()));
    }
}

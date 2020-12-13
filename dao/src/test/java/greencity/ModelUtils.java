package greencity;

import greencity.entity.*;
import greencity.entity.localization.AdviceTranslation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.FactOfDayStatus;
import greencity.enums.TagType;

import java.util.ArrayList;
import java.util.Arrays;
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
            .language(Language.builder().id(1L).code("ua").build())
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
            .content("Тест факт # 1'")
            .language(getLanguageUa())
            .factOfDayStatus(FactOfDayStatus.POTENTIAL)
            .habitFact(null)
            .build();
    }

    public static HabitFact getHabitFact() {
        return HabitFact.builder()
            .id(1L)
            .habit(Habit.builder().id(1L).image("image_one.png")
                .build())
            .build();
    }

    public static Language getLanguageEn() {
        return new Language(2L, "en", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public static Language getLanguageUa() {
        return new Language(1L, "ua", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public static Language getLanguageRu() {
        return new Language(3L, "ru", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public static List<Advice> getAdvices() {
        return new ArrayList<>(List.of(Advice.builder().id(1L)
            .habit(Habit.builder().id(2L).image("image_one.png")
                .build())
            .translations(List.of(getAdviceTranslationFirst()))
            .build(),
            Advice.builder().id(2L)
                .habit(Habit.builder().id(2L).image("image_one.png")
                    .build())
                .translations(List.of(getAdviceTranslationSecond()))
                .build(),
            Advice.builder().id(3L)
                .habit(Habit.builder().id(3L).image("image_one.png")
                    .build())
                .translations(List.of(getAdviceTranslationThird()))
                .build()));
    }

    public static Tag getTagEcoNews() {
        return new Tag(1L, TagType.ECO_NEWS, getTagTranslationsNews(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptySet());
    }

    public static Tag getTagHabit() {
        return new Tag(2L, TagType.HABIT, getTagTranslationsEducation(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptySet());
    }

    public static Tag getTagTipsAndTricks() {
        return new Tag(3L, TagType.TIPS_AND_TRICKS, getTagTranslationsAds(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptySet());
    }

    public static List<TagTranslation> getTagTranslationsNews() {
        return Arrays.asList(
            TagTranslation.builder().id(1L).name("Новини").language(getLanguageUa()).build(),
            TagTranslation.builder().id(2L).name("News").language(getLanguageEn()).build(),
            TagTranslation.builder().id(3L).name("Новины").language(getLanguageRu()).build());
    }

    public static List<TagTranslation> getTagTranslationsEducation() {
        return Arrays.asList(
            TagTranslation.builder().id(4L).name("Освіта").language(getLanguageUa()).build(),
            TagTranslation.builder().id(5L).name("Education").language(getLanguageEn()).build(),
            TagTranslation.builder().id(6L).name("Образование").language(getLanguageRu()).build());
    }

    public static List<TagTranslation> getTagTranslationsAds() {
        return Arrays.asList(
            TagTranslation.builder().id(7L).name("Реклами").language(getLanguageUa()).build(),
            TagTranslation.builder().id(8L).name("Ads").language(getLanguageEn()).build(),
            TagTranslation.builder().id(9L).name("Рекламы").language(getLanguageRu()).build());
    }
}

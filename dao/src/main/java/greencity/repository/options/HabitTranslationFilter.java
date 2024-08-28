package greencity.repository.options;

import greencity.dto.filter.HabitTranslationFilterDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import greencity.enums.HabitAssignStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class HabitTranslationFilter implements Specification<HabitTranslation> {
    private final HabitTranslationFilterDto filter;

    @Override
    public Predicate toPredicate(Root<HabitTranslation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);
        List<Predicate> predicates = buildPredicates(root, criteriaBuilder);
        return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
    }

    private List<Predicate> buildPredicates(Root<HabitTranslation> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        Join<HabitTranslation, Habit> habitJoin = root.join("habit", JoinType.INNER);

        if (!filter.isCustom()) {
            predicates.add(isNotCustomHabit(criteriaBuilder, habitJoin));
        }

        if (filter.isCustom() && filter.getUserId() != null) {
            predicates.add(isCustomHabit(criteriaBuilder, habitJoin, filter.getUserId(), filter.getRequestedIds()));
        }

        if (filter.getLanguageCode() != null && !filter.getLanguageCode().isEmpty()) {
            predicates.add(hasLanguageCode(root, criteriaBuilder, filter.getLanguageCode()));
        }

        if (filter.getComplexities() != null && !filter.getComplexities().isEmpty()) {
            predicates.add(hasComplexity(habitJoin, filter.getComplexities()));
        }

        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            predicates.add(hasTags(criteriaBuilder, habitJoin, filter.getTags()));
        }

        return predicates;
    }

    private Predicate isNotCustomHabit(CriteriaBuilder cb, Join<HabitTranslation, Habit> habitJoin) {
        Predicate isCustomHabitFalse = cb.equal(habitJoin.get("isCustomHabit"), false);
        Predicate isDeletedFalse = cb.equal(habitJoin.get("isDeleted"), false);
        return cb.and(isCustomHabitFalse, isDeletedFalse);
    }

    private Predicate isCustomHabit(CriteriaBuilder cb, Join<HabitTranslation, Habit> habitJoin, Long userId,
        List<Long> requestedIds) {
        Predicate isCustomHabitTrue = cb.equal(habitJoin.get("isCustomHabit"), true);
        Predicate isDeletedFalse = cb.equal(habitJoin.get("isDeleted"), false);

        Predicate isRequested = habitJoin.get("id").in(requestedIds);
        Predicate isAuthor = cb.equal(habitJoin.get("userId"), userId);

        return cb.and(
            isCustomHabitTrue,
            isDeletedFalse,
            cb.or(isRequested, isAuthor));
    }

    private Predicate hasTags(CriteriaBuilder cb, Join<HabitTranslation, Habit> habitJoin, List<String> tags) {
        Join<Habit, Tag> tagJoin = habitJoin.join("tags", JoinType.INNER);
        Join<Tag, TagTranslation> tagTranslationJoin = tagJoin.join("tagTranslations", JoinType.INNER);

        List<String> lowerCaseTags = tags.stream()
            .map(String::toLowerCase)
            .toList();

        return cb.lower(tagTranslationJoin.get("name")).in(lowerCaseTags);
    }

    private Predicate hasComplexity(Join<HabitTranslation, Habit> habitJoin, List<Integer> complexities) {
        return habitJoin.get("complexity").in(complexities);
    }

    private Predicate hasLanguageCode(Root<HabitTranslation> root, CriteriaBuilder cb, String languageCode) {
        Join<HabitTranslation, Language> languageJoin = root.join("language", JoinType.INNER);
        return cb.equal(languageJoin.get("code"), languageCode);
    }
}

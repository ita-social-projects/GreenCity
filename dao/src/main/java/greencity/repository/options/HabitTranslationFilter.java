package greencity.repository.options;

import greencity.dto.filter.HabitTranslationFilterDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
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

        if (filter.getLanguageCode() != null) {
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

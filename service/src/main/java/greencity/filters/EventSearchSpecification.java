package greencity.filters;

import greencity.entity.event.Event;
import greencity.entity.event.Event_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

public class EventSearchSpecification extends EventSpecification {
    private final Map<String, TriFunction<Root<Event>, CriteriaBuilder, SearchCriteria, Predicate>> pred =
        Map.of("text", this::getTextPredicate);

    public EventSearchSpecification(List<SearchCriteria> searchCriteriaList, Long userId) {
        super(searchCriteriaList, userId);
    }

    @Override
    public Predicate toPredicate(@NotNull Root<Event> root,
        @NotNull CriteriaQuery<?> criteriaQuery,
        @NotNull CriteriaBuilder criteriaBuilder) {
        Predicate superPredicate = super.toPredicate(root, criteriaQuery, criteriaBuilder);
        Predicate allPredicates = toPredicateFromMap(root, criteriaBuilder, searchCriteriaList, pred);
        criteriaQuery.distinct(true);
        return criteriaBuilder.and(superPredicate, allPredicates);
    }

    private Predicate getTextPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String text = searchCriteria.getValue().toString().trim();
        if (text.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        ArrayList<Predicate> eachWordLikePredicates = new ArrayList<>();
        Arrays.stream(text.split(" ")).forEach(word -> eachWordLikePredicates.add(
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Event_.TITLE)),
                    "%" + word.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Event_.DESCRIPTION)),
                    "%" + word.toLowerCase() + "%"))));
        return criteriaBuilder.or(eachWordLikePredicates.toArray(new Predicate[0]));
    }
}

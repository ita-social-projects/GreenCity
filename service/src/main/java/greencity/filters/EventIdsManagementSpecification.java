package greencity.filters;

import greencity.entity.event.Event;
import greencity.entity.event.Event_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class EventIdsManagementSpecification extends EventSpecification {
    public EventIdsManagementSpecification(List<SearchCriteria> searchCriteriaList, Long userId) {
        super(searchCriteriaList, userId);
    }

    @Override
    public Predicate toPredicate(@NotNull Root<Event> root,
        @NotNull CriteriaQuery<?> criteriaQuery,
        @NotNull CriteriaBuilder criteriaBuilder) {
        Predicate superPredicate = super.toPredicate(root, criteriaQuery, criteriaBuilder);
        criteriaQuery.multiselect(root.get(Event_.ID));
        return superPredicate;
    }
}

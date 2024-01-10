package greencity.filters;

import static greencity.entity.HabitFactTranslation_.content;
import greencity.entity.*;
import jakarta.persistence.criteria.*;
import java.util.List;

public class HabitFactSpecification implements MySpecification<HabitFact> {
    private transient List<SearchCriteria> searchCriteriaList;

    /**
     * Constructor.
     */
    public HabitFactSpecification(List<SearchCriteria> searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    @Override
    public Predicate toPredicate(Root<HabitFact> root, CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("id")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getNumericPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("habitId")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates,
                        getHabitIdPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("content")) {
                allPredicates = criteriaBuilder.and(allPredicates,
                    getTranslationPredicate(root, criteriaQuery, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicates;
    }

    private Predicate getTranslationPredicate(Root<HabitFact> root, CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder, SearchCriteria searchCriteria) {
        Root<HabitFactTranslation> habitFactTranslationRoot = criteriaQuery.from(HabitFactTranslation.class);
        return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction()
            : criteriaBuilder.and(criteriaBuilder.like(habitFactTranslationRoot.get(content),
                "%" + searchCriteria.getValue() + "%"),
                criteriaBuilder.equal(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact).get(HabitFact_.id),
                    root.get(HabitFact_.id)));
    }

    private Predicate getHabitIdPredicate(Root<HabitFact> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        Join<HabitFact, Habit> habitJoin = root.join(HabitFact_.habit);

        return criteriaBuilder
            .equal(habitJoin.get(Habit_.id), searchCriteria.getValue());
    }
}

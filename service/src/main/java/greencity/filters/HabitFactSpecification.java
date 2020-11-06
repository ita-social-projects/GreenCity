package greencity.filters;

import greencity.entity.Habit;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import javax.persistence.criteria.*;
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
        Root<HabitFactTranslation> titleTranslationRoot = criteriaQuery.from(HabitFactTranslation.class);
        return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction() :
            criteriaBuilder.and(criteriaBuilder.like(titleTranslationRoot.get("content"),
                "%" + searchCriteria.getValue() + "%"),
                criteriaBuilder.equal(titleTranslationRoot.get("habitFact").get("id"), root.get("id")));
    }

    private Predicate getHabitIdPredicate(Root<HabitFact> root, CriteriaBuilder criteriaBuilder,
                                          SearchCriteria searchCriteria) {
        Join<HabitFact, Habit> habitJoin = root.join("habit");

        return criteriaBuilder
            .equal(habitJoin.get("id"), searchCriteria.getValue());
    }
}

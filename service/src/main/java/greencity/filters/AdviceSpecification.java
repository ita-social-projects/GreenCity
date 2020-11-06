package greencity.filters;

import greencity.entity.Advice;
import greencity.entity.Habit;
import greencity.entity.localization.AdviceTranslation;

import javax.persistence.criteria.*;
import java.util.List;

public class AdviceSpecification implements MySpecification<Advice> {
    private transient List<SearchCriteria> searchCriteriaList;

    /**
     * Constructor.
     */
    public AdviceSpecification(List<SearchCriteria> searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    @Override
    public Predicate toPredicate(Root<Advice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        criteriaQuery.distinct(true);

        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("id")) {
                allPredicates = criteriaBuilder.and(allPredicates,
                        getNumericPredicate(root, criteriaBuilder, searchCriteria));
            }

            if (searchCriteria.getType().equals("habitId")) {
                allPredicates = criteriaBuilder.and(allPredicates,
                        getHabitIdPredicate(root, criteriaBuilder, searchCriteria));
            }

            if (searchCriteria.getType().equals("translationContent")) {
                allPredicates = criteriaBuilder.and(allPredicates,
                        getTranslationPredicate(root, criteriaBuilder, searchCriteria));
            }
        }

        return allPredicates;
    }

    private Predicate getTranslationPredicate(Root<Advice> root,
                                              CriteriaBuilder criteriaBuilder, SearchCriteria searchCriteria) {
        Join<Advice, AdviceTranslation> translationJoin = root.join("translations");

        return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction() :
                criteriaBuilder.and(criteriaBuilder.like(translationJoin.get("content"),
                        "%" + searchCriteria.getValue() + "%"),
                        criteriaBuilder.equal(translationJoin.get("advice").get("id"), root.get("id")));
    }

    private Predicate getHabitIdPredicate(Root<Advice> root, CriteriaBuilder criteriaBuilder,
                                          SearchCriteria searchCriteria) {
        Join<Advice, Habit> habitJoin = root.join("habit");

        return criteriaBuilder
                .equal(habitJoin.get("id"), searchCriteria.getValue());
    }
}

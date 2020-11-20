package greencity.filters;

import greencity.entity.*;
import greencity.entity.localization.GoalTranslation;
import greencity.entity.localization.GoalTranslation_;

import javax.persistence.criteria.*;
import java.util.List;

import static greencity.entity.Translation_.content;

public class GoalSpecification implements MySpecification<Goal> {
    private transient List<SearchCriteria> searchCriteriaList;

    /**
     * Constructor.
     */
    public GoalSpecification(List<SearchCriteria> searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    @Override
    public Predicate toPredicate(Root<Goal> root, CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("id")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getNumericPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("content")) {
                allPredicates = criteriaBuilder.and(allPredicates,
                    getTranslationPredicate(root, criteriaQuery, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicates;
    }

    private Predicate getTranslationPredicate(Root<Goal> root, CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder, SearchCriteria searchCriteria) {
        Root<GoalTranslation> goalTranslationroot = criteriaQuery.from(GoalTranslation.class);
        return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction()
            : criteriaBuilder.and(criteriaBuilder.like(goalTranslationroot.get(content),
                "%" + searchCriteria.getValue() + "%"),
                criteriaBuilder.equal(goalTranslationroot.get(GoalTranslation_.goal).get(Goal_.id),
                    root.get(Goal_.id)));
    }
}

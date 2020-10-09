package greencity.filters;

import greencity.entity.TipsAndTricks;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class TipsAndTricksSpecification implements MySpecification<TipsAndTricks> {
    private transient List<SearchCriteria> searchCriteriaList;

    /**
     * Constructor.
     */
    public TipsAndTricksSpecification(List<SearchCriteria> searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    @Override
    public Predicate toPredicate(Root<TipsAndTricks> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("id")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getNumericPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("title")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getStringPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("author")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getAuthorPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("dateRange")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getDataRangePredicate(root, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicates;
    }

    private Predicate getAuthorPredicate(Root<TipsAndTricks> root, CriteriaBuilder criteriaBuilder,
                                         SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(root.get(searchCriteria.getKey()).get("name"),
            "%" + searchCriteria.getValue() + "%");
    }
}

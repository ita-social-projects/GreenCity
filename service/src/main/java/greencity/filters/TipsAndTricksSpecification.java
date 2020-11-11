package greencity.filters;

import static greencity.entity.TitleTranslation_.content;

import greencity.entity.TipsAndTricks;
import greencity.entity.TipsAndTricks_;
import greencity.entity.TitleTranslation;
import greencity.entity.TitleTranslation_;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

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
                    criteriaBuilder.and(allPredicates,
                        getTitleTranslationPredicate(root, criteriaQuery, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("author")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getAuthorPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("dateRange")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates,
                        getDataRangePredicate(root, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicates;
    }

    private Predicate getTitleTranslationPredicate(Root<TipsAndTricks> root, CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder, SearchCriteria searchCriteria) {
        Root<TitleTranslation> titleTranslationRoot = criteriaQuery.from(TitleTranslation.class);
        return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction()
            : criteriaBuilder.and(criteriaBuilder.like(titleTranslationRoot.get(content),
                "%" + searchCriteria.getValue() + "%"),
                criteriaBuilder.equal(titleTranslationRoot.get(TitleTranslation_.tipsAndTricks).get(TipsAndTricks_.id),
                    root.get(TipsAndTricks_.id)));
    }
}

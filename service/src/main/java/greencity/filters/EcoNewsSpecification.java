package greencity.filters;

import greencity.entity.EcoNews;
import greencity.entity.EcoNews_;
import greencity.entity.Tag_;
import greencity.entity.localization.TagTranslation_;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Scope {@code prototype} is used for creation new bean
 * {@link EcoNewsSpecification} every time after new request.
 */
@Component
@Scope("prototype")
@Slf4j
@NoArgsConstructor
public class EcoNewsSpecification implements MySpecification<EcoNews> {
    private transient List<SearchCriteria> searchCriteriaList;

    /**
     * Constructor.
     */
    public EcoNewsSpecification(List<SearchCriteria> searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    @Override
    public Predicate toPredicate(Root<EcoNews> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("id")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getNumericPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("title") || searchCriteria.getType().equals("text")
                || searchCriteria.getType().equals("imagePath") || searchCriteria.getType().equals("source")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getStringPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("author")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getAuthorPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("dateRange")) {
                allPredicates = criteriaBuilder
                    .and(allPredicates, getDataRangePredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("tags")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getTagsPredicate(root, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicates;
    }

    private Predicate getTagsPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(
            root.join(EcoNews_.tags).join(Tag_.tagTranslations).get(TagTranslation_.name).as(String.class),
            "%" + searchCriteria.getValue() + "%");
    }
}

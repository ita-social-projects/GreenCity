package greencity.filters;

import greencity.entity.*;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.ShoppingListItemTranslation_;
import jakarta.persistence.criteria.*;
import java.util.List;
import static greencity.entity.Translation_.content;

public class ShoppingListItemSpecification implements MySpecification<ShoppingListItem> {
    private transient List<SearchCriteria> searchCriteriaList;

    /**
     * Constructor.
     */
    public ShoppingListItemSpecification(List<SearchCriteria> searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    @Override
    public Predicate toPredicate(Root<ShoppingListItem> root, CriteriaQuery<?> criteriaQuery,
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

    private Predicate getTranslationPredicate(Root<ShoppingListItem> root, CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder, SearchCriteria searchCriteria) {
        Root<ShoppingListItemTranslation> itemTranslationroot = criteriaQuery.from(ShoppingListItemTranslation.class);
        return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction()
            : criteriaBuilder.and(criteriaBuilder.like(itemTranslationroot.get(content),
                "%" + searchCriteria.getValue() + "%"),
                criteriaBuilder.equal(
                    itemTranslationroot.get(ShoppingListItemTranslation_.shoppingListItem).get(ShoppingListItem_.id),
                    root.get(ShoppingListItem_.id)));
    }
}

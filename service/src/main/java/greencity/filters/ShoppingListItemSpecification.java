package greencity.filters;

import greencity.entity.ShoppingListItem;
import greencity.entity.ShoppingListItem_;
import greencity.entity.Translation_;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.ShoppingListItemTranslation_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShoppingListItemSpecification implements MySpecification<ShoppingListItem> {
    private final transient List<SearchCriteria> searchCriteriaList;

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
        return searchCriteria.getValue().toString().trim().isEmpty() ? criteriaBuilder.conjunction()
            : criteriaBuilder.and(criteriaBuilder.like(itemTranslationroot.get(Translation_.content),
                "%" + searchCriteria.getValue() + "%"),
                criteriaBuilder.equal(
                    itemTranslationroot.get(ShoppingListItemTranslation_.shoppingListItem).get(ShoppingListItem_.id),
                    root.get(ShoppingListItem_.id)));
    }
}

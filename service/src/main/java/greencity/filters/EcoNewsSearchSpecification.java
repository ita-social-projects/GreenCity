package greencity.filters;

import greencity.entity.EcoNews;
import greencity.entity.EcoNews_;
import greencity.entity.User;
import greencity.entity.User_;
import greencity.utils.SpecificationUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class EcoNewsSearchSpecification implements MySpecification<EcoNews> {
    private final transient List<SearchCriteria> searchCriteriaList;
    private final transient Long userId;

    private final transient Map<String, TriFunction<Root<EcoNews>, CriteriaBuilder, SearchCriteria, Predicate>> pred =
        Map.of("text", this::getTextPredicate,
            "isFavorite", this::getIsFavoritePredicate);

    @Override
    public Predicate toPredicate(@NotNull Root<EcoNews> root,
        CriteriaQuery<?> criteriaQuery,
        @NotNull CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = toPredicateFromMap(root, criteriaBuilder, searchCriteriaList, pred);
        criteriaQuery.distinct(true);
        return allPredicates;
    }

    private Predicate getTextPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String text = searchCriteria.getValue().toString().trim();
        if (text.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        text = SpecificationUtils.escapeSpecialCharacters(text);
        Predicate titlePredicate = criteriaBuilder.like(root.get(EcoNews_.TITLE),
            "%" + text + "%");
        Predicate textPredicate = criteriaBuilder.like(root.get(EcoNews_.TEXT),
            "%" + text + "%");
        Predicate shortInfoPredicate = criteriaBuilder.like(root.get(EcoNews_.SHORT_INFO),
            "%" + text + "%");
        return criteriaBuilder.or(titlePredicate, textPredicate, shortInfoPredicate);
    }

    private Predicate getIsFavoritePredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        Boolean isFavorite = (Boolean) searchCriteria.getValue();
        if (isFavorite == null) {
            return criteriaBuilder.conjunction();
        }

        Join<EcoNews, User> followersJoin = root.join(EcoNews_.FOLLOWERS, JoinType.LEFT);
        return isFavorite
            ? criteriaBuilder.equal(followersJoin.get(User_.ID), userId)
            : criteriaBuilder.notEqual(followersJoin.get(User_.ID), userId);
    }
}

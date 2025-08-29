package greencity.filters;

import greencity.entity.EcoNews;
import greencity.entity.EcoNews_;
import greencity.entity.User;
import greencity.entity.User_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class EcoNewsSearchSpecification implements MySpecification<EcoNews> {
    private final List<SearchCriteria> searchCriteriaList;
    private final Long userId;

    private final Map<String, TriFunction<Root<EcoNews>, CriteriaBuilder, SearchCriteria, Predicate>> pred =
        Map.of(
            "text", this::getTextPredicate,
            "isFavorite", this::getIsFavoritePredicate);

    @Override
    public Predicate toPredicate(@NotNull Root<EcoNews> root, @NotNull CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
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

        Predicate titlePredicate = criteriaBuilder.like(root.join(EcoNews_.title).as(String.class),
            "%" + text + "%");
        Predicate textPredicate = criteriaBuilder.like(root.join(EcoNews_.text).as(String.class),
            "%" + text + "%");
        Predicate shortInfoPredicate = criteriaBuilder.like(root.join(EcoNews_.shortInfo).as(String.class),
            "%" + text + "%");
        return criteriaBuilder.or(titlePredicate, textPredicate, shortInfoPredicate);
    }

    private Predicate getIsFavoritePredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String isFavoriteString = searchCriteria.getValue().toString();
        if (isFavoriteString == null) {
            return criteriaBuilder.conjunction();
        }

        Boolean isFavorite = Boolean.parseBoolean(isFavoriteString);
        SetJoin<EcoNews, User> followersJoin = root.join(EcoNews_.followers);
        if (Boolean.TRUE.equals(isFavorite)) {
            return criteriaBuilder.equal(followersJoin.get(User_.ID), userId);
        } else {
            return criteriaBuilder.notEqual(followersJoin.get(User_.ID), userId);
        }
    }
}

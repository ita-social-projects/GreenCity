package greencity.filters;

import greencity.entity.EcoNews;
import greencity.entity.EcoNews_;
import greencity.entity.Tag_;
import greencity.entity.localization.TagTranslation_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class EcoNewsSpecification implements MySpecification<EcoNews> {
    private final transient List<SearchCriteria> searchCriteriaList;

    // Predicate Creators
    private final transient Map<String, TriFunction<Root<EcoNews>, CriteriaBuilder, SearchCriteria, Predicate>> pred =
        Map.of(
            EcoNews_.ID, this::getNumericPredicate,
            EcoNews_.TITLE, this::getStringPredicate,
            EcoNews_.TEXT, this::getStringPredicate,
            EcoNews_.IMAGE_PATH, this::getStringPredicate,
            EcoNews_.SOURCE, this::getStringPredicate,
            EcoNews_.AUTHOR, this::getAuthorPredicate,
            "dateRange", this::getDataRangePredicate,
            EcoNews_.CREATION_DATE, this::getCreationDatePredicate,
            EcoNews_.TAGS, this::getTagsPredicate,
            EcoNews_.HIDDEN, this::getBooleanPredicate);

    @Override
    public Predicate toPredicate(@NotNull Root<EcoNews> root,
        CriteriaQuery<?> criteriaQuery,
        @NotNull CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = toPredicateFromMap(root, criteriaBuilder, searchCriteriaList, pred);
        criteriaQuery.orderBy(getOrderList(root, criteriaQuery, criteriaBuilder));
        return allPredicates;
    }

    private Predicate getAuthorPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String authorString = searchCriteria.getValue().toString().trim();
        if (authorString.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        return criteriaBuilder.like(root.get(searchCriteria.getKey()).get("name"),
            "%" + searchCriteria.getValue() + "%");
    }

    private Predicate getTagsPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        String[] tags = searchCriteria.getValue().toString().split(",");
        List<Predicate> tagPredicates = new ArrayList<>();
        for (String tag : tags) {
            tagPredicates.add(
                criteriaBuilder.like(
                    root.join(EcoNews_.tags).join(Tag_.tagTranslations).get(TagTranslation_.name)
                        .as(String.class),
                    "%" + tag.trim() + "%"));
        }
        return criteriaBuilder.and(tagPredicates.toArray(new Predicate[0]));
    }

    private Predicate getCreationDatePredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        try {
            String date = (String) searchCriteria.getValue();
            LocalDate localDate = LocalDate.parse(date);
            ZonedDateTime zdt1 = localDate.atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime zdt2 = ZonedDateTime.of(LocalDateTime.of(localDate, LocalTime.MAX), ZoneOffset.UTC);
            return criteriaBuilder.between(root.get(searchCriteria.getKey()), zdt1, zdt2);
        } catch (DateTimeParseException ex) {
            return criteriaBuilder.disjunction();
        }
    }

    private List<Order> getOrderList(Root<EcoNews> root, CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
        List<Order> orderList = new ArrayList<>();
        for (Order order : criteriaQuery.getOrderList()) {
            String sortField = order.getExpression().toString();
            if (sortField.equals("likes")) {
                orderList
                    .add(order.isAscending() ? criteriaBuilder.asc(criteriaBuilder.size(root.get("usersLikedNews")))
                        : criteriaBuilder.desc(criteriaBuilder.size(root.get("usersLikedNews"))));
            } else if (sortField.equals("dislikes")) {
                orderList
                    .add(order.isAscending() ? criteriaBuilder.asc(criteriaBuilder.size(root.get("usersDislikedNews")))
                        : criteriaBuilder.desc(criteriaBuilder.size(root.get("usersDislikedNews"))));
            } else {
                orderList.add(order.isAscending() ? criteriaBuilder.asc(root.get(sortField))
                    : criteriaBuilder.desc(root.get(sortField)));
            }
        }
        return orderList;
    }
}

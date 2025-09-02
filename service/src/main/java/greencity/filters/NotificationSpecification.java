package greencity.filters;

import greencity.entity.Notification;
import greencity.entity.Notification_;
import greencity.entity.User;
import greencity.entity.User_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class NotificationSpecification implements MySpecification<Notification> {
    private final List<SearchCriteria> searchCriteriaList;

    private final Map<String, TriFunction<Root<Notification>, CriteriaBuilder, SearchCriteria, Predicate>> pred =
        Map.of(
            Notification_.TARGET_USER, this::getTargetUserPredicate,
            Notification_.PROJECT_NAME, this::getEnumPredicate,
            Notification_.NOTIFICATION_TYPE, this::getNotificationTypePredicate,
            Notification_.VIEWED, this::getBooleanPredicate);

    @Override
    public Predicate toPredicate(@NotNull Root<Notification> root,
        @NotNull CriteriaQuery<?> criteriaQuery,
        @NotNull CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = toPredicateFromMap(root, criteriaBuilder, searchCriteriaList, pred);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Notification_.TIME)));
        return allPredicates;
    }

    private Predicate getTargetUserPredicate(Root<Notification> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String targetUserId = searchCriteria.getValue().toString().trim();
        if (targetUserId.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        Join<Notification, User> targetUserJoin = root.join(Notification_.TARGET_USER);
        return criteriaBuilder.equal(targetUserJoin.get(User_.ID), targetUserId);
    }

    private Predicate getNotificationTypePredicate(Root<Notification> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String typesString = searchCriteria.getValue().toString().trim();
        String[] types = typesString.split(",");
        if (typesString.isEmpty() || types.length == 0) {
            return criteriaBuilder.conjunction();
        }

        List<Predicate> typePredicates = new ArrayList<>();
        for (String type : types) {
            Predicate typePredicate = criteriaBuilder.equal(
                root.get(Notification_.NOTIFICATION_TYPE).as(String.class), "%" + type.trim() + "%");
            typePredicates.add(typePredicate);
        }
        return criteriaBuilder.or(typePredicates.toArray(new Predicate[0]));
    }
}

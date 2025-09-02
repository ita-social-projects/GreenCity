package greencity.filters;

import greencity.entity.Notification;
import greencity.entity.Notification_;
import greencity.entity.User;
import greencity.entity.User_;
import greencity.enums.NotificationType;
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
    private final transient List<SearchCriteria> searchCriteriaList;

    private final transient Map<String, TriFunction<Root<Notification>, CriteriaBuilder, SearchCriteria, Predicate>> pred =
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
        Long targetUserId = (Long) searchCriteria.getValue();
        if (targetUserId == null) {
            return criteriaBuilder.conjunction();
        }

        Join<Notification, User> targetUserJoin = root.join(Notification_.TARGET_USER);
        return criteriaBuilder.equal(targetUserJoin.get(User_.ID), targetUserId);
    }

    private Predicate getNotificationTypePredicate(Root<Notification> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        List<NotificationType> types = (List<NotificationType>) searchCriteria.getValue();
        if (types == null || types.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        List<Predicate> typePredicates = new ArrayList<>();
        for (NotificationType type : types) {
            Predicate typePredicate = criteriaBuilder.equal(
                root.get(Notification_.NOTIFICATION_TYPE).as(String.class), "%" + type.name() + "%");
            typePredicates.add(typePredicate);
        }
        return criteriaBuilder.or(typePredicates.toArray(new Predicate[0]));
    }
}

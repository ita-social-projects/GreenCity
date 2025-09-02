package greencity.filters;

import greencity.entity.User_;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventDateLocation_;
import greencity.entity.event.Event_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class EventIdsSpecification extends EventIdsManagementSpecification {
    public EventIdsSpecification(List<SearchCriteria> searchCriteriaList, Long userId) {
        super(searchCriteriaList, userId);
    }

    @Override
    public Predicate toPredicate(@NotNull Root<Event> root,
        @NotNull CriteriaQuery<?> criteriaQuery,
        @NotNull CriteriaBuilder criteriaBuilder) {
        Predicate superPredicate = super.toPredicate(root, criteriaQuery, criteriaBuilder);
        criteriaQuery.orderBy(getOrders(root, criteriaBuilder));
        return superPredicate;
    }

    private List<Order> getOrders(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        List<Order> orders = new ArrayList<>();

        if (userId != null) {
            addSortByOrganizerOrder(root, criteriaBuilder, orders);
            addSortByFollowersOrder(root, criteriaBuilder, orders);
            addSortByAttendersOrder(root, criteriaBuilder, orders);
        }

        Join<Event, EventDateLocation> datesJoin = root.join(Event_.DATES, JoinType.LEFT);
        addSortByCurrentDateOrder(criteriaBuilder, orders, datesJoin);
        addSortByOneWeekOrder(criteriaBuilder, orders, datesJoin);
        addSortByDateOrder(criteriaBuilder, orders, datesJoin);

        return orders;
    }

    private void addSortByOrganizerOrder(Root<Event> root, CriteriaBuilder criteriaBuilder, List<Order> orders) {
        orders.add(criteriaBuilder.desc(criteriaBuilder.selectCase()
            .when(criteriaBuilder.equal(
                root.get(Event_.ORGANIZER).get(User_.ID), userId), 1)
            .otherwise(0)));
    }

    private void addSortByFollowersOrder(Root<Event> root, CriteriaBuilder criteriaBuilder, List<Order> orders) {
        orders.add(criteriaBuilder.desc(criteriaBuilder.selectCase()
            .when(criteriaBuilder.equal(
                root.join(Event_.FOLLOWERS, JoinType.LEFT).get(User_.ID), userId), 1)
            .otherwise(0)));
    }

    private void addSortByAttendersOrder(Root<Event> root, CriteriaBuilder criteriaBuilder, List<Order> orders) {
        orders.add(criteriaBuilder.desc(criteriaBuilder.selectCase()
            .when(criteriaBuilder.equal(
                root.join(Event_.ATTENDERS, JoinType.LEFT).get(User_.ID), userId), 1)
            .otherwise(0)));
    }

    private void addSortByOneWeekOrder(CriteriaBuilder criteriaBuilder,
        List<Order> orders, Join<Event, EventDateLocation> datesJoin) {
        ZonedDateTime currentDate = ZonedDateTime.now();
        ZonedDateTime oneWeekLater = currentDate.plusWeeks(1);
        orders.add(criteriaBuilder.desc(criteriaBuilder.selectCase()
            .when(criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(datesJoin.get(EventDateLocation_.START_DATE), currentDate),
                criteriaBuilder.lessThanOrEqualTo(datesJoin.get(EventDateLocation_.START_DATE), oneWeekLater)), 1)
            .otherwise(0)));
    }

    private void addSortByCurrentDateOrder(CriteriaBuilder criteriaBuilder,
        List<Order> orders, Join<Event, EventDateLocation> datesJoin) {
        orders.add(criteriaBuilder.desc(criteriaBuilder.selectCase()
            .when(criteriaBuilder.equal(
                criteriaBuilder.function("DATE", Date.class, datesJoin.get(EventDateLocation_.START_DATE)),
                criteriaBuilder.function("DATE", Date.class, criteriaBuilder.currentDate())), 1)
            .otherwise(0)));
    }

    private void addSortByDateOrder(CriteriaBuilder criteriaBuilder,
        List<Order> orders, Join<Event, EventDateLocation> datesJoin) {
        orders.add(criteriaBuilder.desc(datesJoin.get(EventDateLocation_.START_DATE)));
    }
}

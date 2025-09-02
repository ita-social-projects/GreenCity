package greencity.filters;

import greencity.entity.Tag;
import greencity.entity.Tag_;
import greencity.entity.User;
import greencity.entity.User_;
import greencity.entity.event.Address;
import greencity.entity.event.Address_;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventDateLocation_;
import greencity.entity.event.Event_;
import greencity.entity.localization.TagTranslation;
import greencity.entity.localization.TagTranslation_;
import greencity.enums.EventStatus;
import greencity.enums.EventTime;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class EventSpecification implements MySpecification<Event> {
    protected final transient List<SearchCriteria> searchCriteriaList;
    protected final transient Long userId;

    private final transient Map<String, TriFunction<Root<Event>, CriteriaBuilder, SearchCriteria, Predicate>> pred =
        Map.of(
            "eventTime", this::getEventTimePredicate,
            "cities", this::getCitiesPredicate,
            "statuses", this::getStatusesPredicate,
            Event_.TAGS, this::getTagsPredicate,
            Event_.TITLE, this::getStringPredicate,
            Event_.TYPE, this::getEnumPredicate,
            "dateRange", this::getDatePredicate,
            "isFavorite", this::getIsFavoritePredicate);

    @Override
    public Predicate toPredicate(@NotNull Root<Event> root,
        @NotNull CriteriaQuery<?> criteriaQuery,
        @NotNull CriteriaBuilder criteriaBuilder) {
        return toPredicateFromMap(root, criteriaBuilder, searchCriteriaList, pred);
    }

    private Predicate getEventTimePredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        EventTime eventTime = (EventTime) searchCriteria.getValue();
        if (eventTime == null) {
            return criteriaBuilder.conjunction();
        }

        Join<Event, EventDateLocation> datesJoin = root.join(Event_.DATES, JoinType.LEFT);
        if (eventTime.equals(EventTime.UPCOMING)) {
            return criteriaBuilder.greaterThan(datesJoin.get(EventDateLocation_.FINISH_DATE), ZonedDateTime.now());
        } else if (eventTime.equals(EventTime.PAST)) {
            return criteriaBuilder.lessThan(datesJoin.get(EventDateLocation_.FINISH_DATE), ZonedDateTime.now());
        } else {
            return criteriaBuilder.disjunction();
        }
    }

    private Predicate getCitiesPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String[] cities = (String[]) searchCriteria.getValue();
        if (cities == null || cities.length == 0) {
            return criteriaBuilder.conjunction();
        }

        cities = arrayToUpperCase(cities);
        Join<EventDateLocation, Address> addressJoin = root.join(Event_.DATES, JoinType.LEFT)
            .join(EventDateLocation_.ADDRESS);
        Predicate citiesEn = criteriaBuilder.upper(addressJoin.get(Address_.CITY_EN)).in((Object[]) cities);
        Predicate citiesUk = criteriaBuilder.upper(addressJoin.get(Address_.CITY_UK)).in((Object[]) cities);
        return criteriaBuilder.or(citiesEn, citiesUk);
    }

    private Predicate getStatusesPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        EventStatus[] statuses = (EventStatus[]) searchCriteria.getValue();
        if (statuses == null || statuses.length == 0) {
            return criteriaBuilder.conjunction();
        }

        List<Predicate> statusesPredicate = new ArrayList<>();
        Arrays.stream(statuses).forEach(status -> {
            if (status.equals(EventStatus.OPEN)) {
                statusesPredicate.add(criteriaBuilder.isTrue(root.get(Event_.IS_OPEN)));
            } else if (status.equals(EventStatus.CLOSED)) {
                statusesPredicate.add(criteriaBuilder.isFalse(root.get(Event_.IS_OPEN)));
            } else if (status.equals(EventStatus.CREATED) && userId != null) {
                Join<Event, User> organizerJoin = root.join(Event_.ORGANIZER, JoinType.LEFT);
                statusesPredicate.add(criteriaBuilder.equal(organizerJoin.get(User_.ID), userId));
            } else if (status.equals(EventStatus.JOINED) && userId != null) {
                Join<Event, User> attendersJoin = root.join(Event_.ATTENDERS, JoinType.LEFT);
                statusesPredicate.add(criteriaBuilder.equal(attendersJoin.get(User_.ID), userId));
            } else if (status.equals(EventStatus.SAVED) && userId != null) {
                Join<Event, User> followersJoin = root.join(Event_.FOLLOWERS, JoinType.LEFT);
                statusesPredicate.add(criteriaBuilder.equal(followersJoin.get(User_.ID), userId));
            }
        });

        if (!statusesPredicate.isEmpty()) {
            return criteriaBuilder.or(statusesPredicate.toArray(new Predicate[0]));
        } else {
            return criteriaBuilder.disjunction();
        }
    }

    private Predicate getTagsPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String[] tags = (String[]) searchCriteria.getValue();
        if (tags == null || tags.length == 0) {
            return criteriaBuilder.conjunction();
        }

        tags = arrayToUpperCase(tags);
        Join<Tag, TagTranslation> tagsJoin = root.join(Event_.TAGS).join(Tag_.TAG_TRANSLATIONS);
        return criteriaBuilder.upper(tagsJoin.get(TagTranslation_.NAME)).in((Object[]) tags);
    }

    private Predicate getDatePredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        ZonedDateTime[] dates = (ZonedDateTime[]) searchCriteria.getValue();
        if (dates == null || dates.length == 0) {
            return criteriaBuilder.conjunction();
        }

        Join<Event, EventDateLocation> datesJoin = root.join(Event_.DATES, JoinType.LEFT);
        Predicate finalPredicate = criteriaBuilder.conjunction();
        if (dates[0] != null) {
            ZonedDateTime from = dates[0].truncatedTo(ChronoUnit.DAYS);
            Predicate startDatePredicate =
                criteriaBuilder.greaterThanOrEqualTo(datesJoin.get(EventDateLocation_.START_DATE), from);
            finalPredicate = criteriaBuilder.and(finalPredicate, startDatePredicate);
        }

        if (dates.length > 1 && dates[1] != null) {
            ZonedDateTime to = dates[1].truncatedTo(ChronoUnit.DAYS);
            Predicate finishDatePredicate =
                criteriaBuilder.lessThanOrEqualTo(datesJoin.get(EventDateLocation_.FINISH_DATE), to);
            finalPredicate = criteriaBuilder.and(finalPredicate, finishDatePredicate);
        }

        return finalPredicate;
    }

    private Predicate getIsFavoritePredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        Boolean isFavorite = (Boolean) searchCriteria.getValue();
        if (isFavorite == null) {
            return criteriaBuilder.conjunction();
        }

        Join<Event, User> followersJoin = root.join(Event_.FOLLOWERS);
        return isFavorite
            ? criteriaBuilder.equal(followersJoin.get(User_.ID), userId)
            : criteriaBuilder.notEqual(followersJoin.get(User_.ID), userId);
    }

    private String[] arrayToUpperCase(String[] objects) {
        return Arrays.stream(objects)
            .map(String::toUpperCase)
            .toArray(String[]::new);
    }
}

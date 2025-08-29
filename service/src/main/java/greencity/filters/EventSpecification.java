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
import jakarta.persistence.criteria.SetJoin;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class EventSpecification implements MySpecification<Event> {
    protected final List<SearchCriteria> searchCriteriaList;
    protected final Long userId;

    private final Map<String, TriFunction<Root<Event>, CriteriaBuilder, SearchCriteria, Predicate>> pred =
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
        String eventTime = searchCriteria.getValue().toString().trim();
        if (eventTime.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        Join<Event, EventDateLocation> datesJoin = root.join(Event_.DATES, JoinType.LEFT);
        if (eventTime.equals(EventTime.UPCOMING.name())) {
            return criteriaBuilder.greaterThan(datesJoin.get(EventDateLocation_.FINISH_DATE), ZonedDateTime.now());
        } else if (eventTime.equals(EventTime.PAST.name())) {
            return criteriaBuilder.lessThan(datesJoin.get(EventDateLocation_.FINISH_DATE), ZonedDateTime.now());
        } else {
            return criteriaBuilder.disjunction();
        }
    }

    private Predicate getCitiesPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String[] cities = searchCriteria.getValue().toString().trim().split(",");
        if (cities.length == 0) {
            return criteriaBuilder.conjunction();
        }

        String[] citiesInUpperCase = listToUpperCase(cities);
        Join<EventDateLocation, Address> addressJoin = root.join(Event_.DATES, JoinType.LEFT)
            .join(EventDateLocation_.ADDRESS);
        Predicate citiesEn = criteriaBuilder.upper(addressJoin.get(Address_.CITY_EN)).in((Object[]) citiesInUpperCase);
        Predicate citiesUk = criteriaBuilder.upper(addressJoin.get(Address_.CITY_UK)).in((Object[]) citiesInUpperCase);
        return criteriaBuilder.or(citiesEn, citiesUk);
    }

    private Predicate getStatusesPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String[] statuses = searchCriteria.getValue().toString().trim().split(",");
        if (statuses.length == 0) {
            return criteriaBuilder.conjunction();
        }

        List<Predicate> statusesPredicate = new ArrayList<>();
        Arrays.stream(statuses).forEach(status -> {
            if (status.equals(EventStatus.OPEN.name())) {
                statusesPredicate.add(criteriaBuilder.isTrue(root.get(Event_.IS_OPEN)));
            } else if (status.equals(EventStatus.CLOSED.name())) {
                statusesPredicate.add(criteriaBuilder.isFalse(root.get(Event_.IS_OPEN)));
            } else if (status.equals(EventStatus.CREATED.name()) && userId != null) {
                Join<Event, User> organizerJoin = root.join(Event_.organizer, JoinType.LEFT);
                statusesPredicate.add(criteriaBuilder.equal(organizerJoin.get(User_.ID), userId));
            } else if (status.equals(EventStatus.JOINED.name()) && userId != null) {
                SetJoin<Event, User> attendersJoin = root.join(Event_.attenders, JoinType.LEFT);
                statusesPredicate.add(criteriaBuilder.equal(attendersJoin.get(User_.ID), userId));
            } else if (status.equals(EventStatus.SAVED.name()) && userId != null) {
                SetJoin<Event, User> followersJoin = root.join(Event_.followers, JoinType.LEFT);
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
        String[] tags = searchCriteria.getValue().toString().trim().split(",");
        if (tags.length == 0) {
            return criteriaBuilder.conjunction();
        }

        String[] tagsInUpperCase = listToUpperCase(tags);
        Join<Tag, TagTranslation> tagsJoin = root.join(Event_.TAGS).join(Tag_.TAG_TRANSLATIONS);
        return criteriaBuilder.upper(tagsJoin.get(TagTranslation_.NAME)).in((Object[]) tagsInUpperCase);
    }

    private Predicate getDatePredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String[] dateRange = searchCriteria.getValue().toString().trim().split(",");
        if (dateRange.length == 0) {
            return criteriaBuilder.conjunction();
        }

        Join<Event, EventDateLocation> datesJoin = root.join(Event_.DATES, JoinType.LEFT);
        Predicate finalPredicate = criteriaBuilder.conjunction();
        if (dateRange.length == 1) {
            ZonedDateTime from = ZonedDateTime.parse(dateRange[0]).withHour(0);
            Predicate startDatePredicate =
                criteriaBuilder.greaterThanOrEqualTo(datesJoin.get(EventDateLocation_.START_DATE), from);
            finalPredicate = criteriaBuilder.and(finalPredicate, startDatePredicate);
        }
        if (dateRange.length == 2) {
            ZonedDateTime to = ZonedDateTime.parse(dateRange[1]).withHour(0);
            Predicate finishDatePredicate =
                criteriaBuilder.lessThanOrEqualTo(datesJoin.get(EventDateLocation_.FINISH_DATE), to);
            finalPredicate = criteriaBuilder.and(finalPredicate, finishDatePredicate);
        }

        return finalPredicate;
    }

    private Predicate getIsFavoritePredicate(Root<Event> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String isFavoriteString = searchCriteria.getValue().toString().trim();
        if (isFavoriteString.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        boolean isFavorite = Boolean.parseBoolean(isFavoriteString);
        Join<Event, User> followersJoin = root.join(Event_.FOLLOWERS);
        return isFavorite
            ? criteriaBuilder.equal(followersJoin.get(User_.ID), userId)
            : criteriaBuilder.notEqual(followersJoin.get(User_.ID), userId);
    }

    private String[] listToUpperCase(String[] objects) {
        return Arrays.stream(objects)
            .map(String::toUpperCase)
            .toArray(String[]::new);
    }
}

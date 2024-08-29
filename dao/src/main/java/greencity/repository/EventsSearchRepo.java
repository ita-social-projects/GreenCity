package greencity.repository;

import greencity.dto.filter.FilterEventDto;
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
import greencity.enums.EventType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;
import jakarta.persistence.criteria.Subquery;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class EventsSearchRepo {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    /**
     * Initialization constructor.
     */
    public EventsSearchRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    /**
     * Method for search list event ids by {@link FilterEventDto}.
     *
     * @param pageable       {@link Pageable}.
     * @param filterEventDto {@link FilterEventDto}.
     * @return list of event ids.
     */
    public Page<Long> findEventsIds(Pageable pageable, FilterEventDto filterEventDto) {
        CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
        Root<Event> eventRoot = criteria.from(Event.class);

        criteria.multiselect(eventRoot.get(Event_.ID))
            .where(getPredicate(filterEventDto, eventRoot))
            .orderBy(getOrders(filterEventDto, eventRoot));

        List<Long> resultList = entityManager.createQuery(criteria).getResultList();
        List<Long> uniqueEventIds = resultList.stream().distinct().toList();

        return buildPage(uniqueEventIds, pageable);
    }

    private PageImpl<Long> buildPage(List<Long> uniqueEventIds, Pageable pageable) {
        int totalElements = uniqueEventIds.size();
        int fromIndex = pageable.getPageNumber() * pageable.getPageSize();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), totalElements);

        List<Long> pagedEventIds = fromIndex < totalElements
            ? uniqueEventIds.subList(fromIndex, toIndex)
            : new ArrayList<>();

        return new PageImpl<>(pagedEventIds, pageable, totalElements);
    }

    /**
     * Method for search events by title,text,short info and tag name.
     *
     * @param pageable      {@link Pageable}
     * @param searchingText - text criteria for searching.
     * @param languageCode  {@link String}.
     * @return all finding events, their tags and also count of finding events.
     * @author Anton Bondar
     */
    public Page<Event> find(Pageable pageable, String searchingText, String languageCode) {
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        Predicate predicate = getPredicate(searchingText, languageCode, root);
        criteriaQuery.select(root).distinct(true).where(predicate);

        TypedQuery<Event> typedQuery = entityManager.createQuery(criteriaQuery)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        List<Event> resultList = typedQuery.getResultList();
        long total = getEventsCount(searchingText, languageCode);

        return new PageImpl<>(resultList, pageable, total);
    }

    private Predicate getPredicate(String searchingText, String languageCode, Root<Event> root) {
        List<Predicate> predicateList = formEventsLikePredicate(searchingText, root);
        predicateList.add(formTagTranslationsPredicate(searchingText, languageCode, root));
        return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
    }

    private Predicate getPredicate(FilterEventDto filterEventDto, Root<Event> eventRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (filterEventDto != null) {
            addEventTimePredicate(filterEventDto.getTime(), eventRoot, predicates);
            addCitiesPredicate(filterEventDto.getCities(), eventRoot, predicates);
            addStatusesPredicate(filterEventDto.getStatuses(), filterEventDto.getUserId(), eventRoot, predicates);
            addTagsPredicate(filterEventDto.getTags(), eventRoot, predicates);
            addTitlePredicate(filterEventDto.getTitle(), eventRoot, predicates);
            addTypePredicate(filterEventDto.getType(), eventRoot, predicates);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void addEventTimePredicate(EventTime eventTime, Root<Event> eventRoot, List<Predicate> predicates) {
        if (eventTime != null) {
            ListJoin<Event, EventDateLocation> datesJoin = eventRoot.join(Event_.dates);
            if (eventTime == EventTime.FUTURE) {
                predicates.add(
                    criteriaBuilder.greaterThan(datesJoin.get(EventDateLocation_.FINISH_DATE), ZonedDateTime.now()));
            } else if (eventTime == EventTime.PAST) {
                predicates.add(
                    criteriaBuilder.lessThan(datesJoin.get(EventDateLocation_.FINISH_DATE), ZonedDateTime.now()));
            }
        }
    }

    private void addCitiesPredicate(List<String> cities, Root<Event> eventRoot, List<Predicate> predicates) {
        if (cities != null && !cities.isEmpty()) {
            Join<EventDateLocation, Address> addressJoin = eventRoot
                .join(Event_.dates).join(EventDateLocation_.address);
            predicates.add(addressJoin.get(Address_.CITY_EN).in(cities));
        }
    }

    private void addStatusesPredicate(List<EventStatus> eventStatuses, Long userId, Root<Event> eventRoot,
        List<Predicate> predicates) {
        if (eventStatuses != null) {
            List<Predicate> statusesPredicate = new ArrayList<>();
            eventStatuses.forEach(status -> {
                if (status == EventStatus.OPEN) {
                    statusesPredicate.add(criteriaBuilder.isTrue(eventRoot.get(Event_.IS_OPEN)));
                } else if (status == EventStatus.CLOSED) {
                    statusesPredicate.add(criteriaBuilder.isFalse(eventRoot.get(Event_.IS_OPEN)));
                } else if (status == EventStatus.CREATED && userId != null) {
                    Join<Event, User> organizerJoin = eventRoot.join(Event_.organizer);
                    statusesPredicate.add(criteriaBuilder.equal(organizerJoin.get(User_.ID), userId));
                } else if (status == EventStatus.JOINED && userId != null) {
                    SetJoin<Event, User> attendersJoin = eventRoot.join(Event_.attenders);
                    statusesPredicate.add(criteriaBuilder.equal(attendersJoin.get(User_.ID), userId));
                } else if (status == EventStatus.SAVED && userId != null) {
                    SetJoin<Event, User> followersJoin = eventRoot.join(Event_.followers);
                    statusesPredicate.add(criteriaBuilder.equal(followersJoin.get(User_.ID), userId));
                }
            });
            predicates.add(criteriaBuilder.or(statusesPredicate.toArray(new Predicate[0])));
        }
    }

    private void addTagsPredicate(List<String> tags, Root<Event> eventRoot, List<Predicate> predicates) {
        if (tags != null && !tags.isEmpty()) {
            ListJoin<Tag, TagTranslation> tagsJoin = eventRoot.join(Event_.tags).join(Tag_.tagTranslations);
            predicates.add(tagsJoin.get(TagTranslation_.NAME).in(tags));
        }
    }

    private void addTitlePredicate(String title, Root<Event> eventRoot, List<Predicate> predicates) {
        if (title != null && !title.isEmpty()) {
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(eventRoot.get(Event_.TITLE)), "%" + title.toLowerCase() + "%"));
        }
    }

    private void addTypePredicate(EventType type, Root<Event> eventRoot, List<Predicate> predicates) {
        if (type != null) {
            predicates.add(criteriaBuilder.equal(eventRoot.get(Event_.TYPE), type));
        }
    }

    private List<Order> getOrders(FilterEventDto filterEventDto, Root<Event> eventRoot) {
        List<Order> orders = new ArrayList<>();

        if (filterEventDto != null && filterEventDto.getUserId() != null) {
            addSortByOrganizerOrder(filterEventDto.getUserId(), eventRoot, orders);
            addSortByFollowersOrder(filterEventDto.getUserId(), eventRoot, orders);
            addSortByAttendersOrder(filterEventDto.getUserId(), eventRoot, orders);
        }

        ListJoin<Event, EventDateLocation> datesJoin = eventRoot.join(Event_.dates);
        addSortByCurrentDateOrder(orders, datesJoin);
        addSortByOneWeekOrder(orders, datesJoin);
        addSortByDateOrder(orders, datesJoin);

        return orders;
    }

    private void addSortByOrganizerOrder(Long userId, Root<Event> eventRoot, List<Order> orders) {
        orders.add(criteriaBuilder.desc(criteriaBuilder.selectCase()
            .when(criteriaBuilder.equal(
                eventRoot.get(Event_.organizer).get(User_.ID), userId), 1)
            .otherwise(0)));
    }

    private void addSortByFollowersOrder(Long userId, Root<Event> eventRoot, List<Order> orders) {
        orders.add(criteriaBuilder.desc(criteriaBuilder.selectCase()
            .when(criteriaBuilder.equal(
                eventRoot.join(Event_.followers, JoinType.LEFT).get(User_.ID), userId), 1)
            .otherwise(0)));
    }

    private void addSortByAttendersOrder(Long userId, Root<Event> eventRoot, List<Order> orders) {
        orders.add(criteriaBuilder.desc(criteriaBuilder.selectCase()
            .when(criteriaBuilder.equal(
                eventRoot.join(Event_.attenders, JoinType.LEFT).get(User_.ID), userId), 1)
            .otherwise(0)));
    }

    private void addSortByOneWeekOrder(List<Order> orders, ListJoin<Event, EventDateLocation> datesJoin) {
        ZonedDateTime currentDate = ZonedDateTime.now();
        ZonedDateTime oneWeekLater = currentDate.plusWeeks(1);
        orders.add(criteriaBuilder.desc(criteriaBuilder.selectCase()
            .when(criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(datesJoin.get(EventDateLocation_.START_DATE), currentDate),
                criteriaBuilder.lessThanOrEqualTo(datesJoin.get(EventDateLocation_.START_DATE), oneWeekLater)), 1)
            .otherwise(0)));
    }

    private void addSortByCurrentDateOrder(List<Order> orders, ListJoin<Event, EventDateLocation> datesJoin) {
        orders.add(criteriaBuilder.desc(criteriaBuilder.selectCase()
            .when(criteriaBuilder.equal(
                criteriaBuilder.function("DATE", Date.class, datesJoin.get(EventDateLocation_.START_DATE)),
                criteriaBuilder.function("DATE", Date.class, criteriaBuilder.currentDate())), 1)
            .otherwise(0)));
    }

    private void addSortByDateOrder(List<Order> orders, ListJoin<Event, EventDateLocation> datesJoin) {
        orders.add(criteriaBuilder.desc(datesJoin.get(EventDateLocation_.START_DATE)));
    }

    private long getEventsCount(String searchingText, String languageCode) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Event> countRoot = countQuery.from(Event.class);

        Predicate countPredicate = getPredicate(searchingText, languageCode, countRoot);
        countQuery.select(criteriaBuilder.count(countRoot)).where(countPredicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Predicate> formEventsLikePredicate(String searchingText, Root<Event> root) {
        Expression<String> title = root.get("title").as(String.class);
        Expression<String> description = root.get("description").as(String.class);

        List<Predicate> predicateList = new ArrayList<>();
        Arrays.stream(searchingText.split(" ")).forEach(partOfSearchingText -> predicateList.add(
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(title), "%" + partOfSearchingText.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(description),
                    "%" + partOfSearchingText.toLowerCase() + "%"))));
        return predicateList;
    }

    private Predicate formTagTranslationsPredicate(String searchingText, String languageCode, Root<Event> root) {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Subquery<Long> tagSubquery = criteriaQuery.subquery(Long.class);
        Root<Tag> tagRoot = tagSubquery.from(Tag.class);
        Join<Event, Tag> eventsTagJoin = tagRoot.join("events");

        Subquery<TagTranslation> tagTranslationSubquery = criteriaQuery.subquery(TagTranslation.class);
        Root<Tag> tagTranslationRoot = tagTranslationSubquery.correlate(tagRoot);

        Join<TagTranslation, Tag> tagTranslationTagJoin = tagTranslationRoot.join("tagTranslations");

        Predicate predicate = predicateForTags(searchingText, languageCode, tagTranslationTagJoin);
        tagTranslationSubquery.select(tagTranslationTagJoin.get("id"))
            .where(predicate);

        tagSubquery.select(eventsTagJoin.get("id")).where(criteriaBuilder.exists(tagTranslationSubquery));
        return criteriaBuilder.in(root.get("id")).value(tagSubquery);
    }

    private Predicate predicateForTags(String searchingText, String languageCode,
        Join<TagTranslation, Tag> tagTranslationTagJoin) {
        List<Predicate> predicateList = new ArrayList<>();
        Arrays.stream(searchingText.split(" ")).forEach(partOfSearchingText -> predicateList.add(criteriaBuilder.and(
            criteriaBuilder.like(criteriaBuilder.lower(tagTranslationTagJoin.get("name")),
                "%" + partOfSearchingText.toLowerCase() + "%"),
            criteriaBuilder.like(criteriaBuilder.lower(tagTranslationTagJoin.get("language").get("code")),
                "%" + languageCode.toLowerCase() + "%"))));
        return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
    }
}

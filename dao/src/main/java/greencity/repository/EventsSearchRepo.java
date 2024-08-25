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
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.SetJoin;
import java.time.ZonedDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * Method for search list event ids by {@link FilterEventDto} and title.
     *
     * @param pageable       {@link Pageable}.
     * @param userId         user id.
     * @param filterEventDto {@link FilterEventDto}.
     * @param title          title of event.
     *
     * @return list of event ids.
     */
    public Page<Long> findEventsIds(Pageable pageable, Long userId, FilterEventDto filterEventDto, String title) {
        CriteriaQuery<Tuple> criteria = criteriaBuilder.createQuery(Tuple.class);

        Subquery<Long> countSubquery = criteria.subquery(Long.class);
        Root<Event> countRoot = countSubquery.from(Event.class);
        Predicate subqueryPredicate = getPredicate(userId, filterEventDto, title, countRoot);
        countSubquery.select(criteriaBuilder.countDistinct(countRoot)).where(subqueryPredicate);

        Root<Event> eventRoot = criteria.from(Event.class);
        Predicate mainPredicate = getPredicate(userId, filterEventDto, title, eventRoot);
        criteria.multiselect(eventRoot.get(Event_.ID), countSubquery.getSelection())
            .distinct(true)
            .where(mainPredicate);

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteria)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        List<Tuple> resultList = typedQuery.getResultList();

        return new PageImpl<>(getEventIdsFromTuple(resultList), pageable, getEventCountFromTuple(resultList));
    }

    private List<Long> getEventIdsFromTuple(List<Tuple> tuples) {
        List<Long> eventIds = new ArrayList<>();
        if (!tuples.isEmpty()) {
            for (Tuple tuple : tuples) {
                eventIds.add(tuple.get(0, Long.class));
            }
        }
        return eventIds;
    }

    private Long getEventCountFromTuple(List<Tuple> tuple) {
        if (!tuple.isEmpty()) {
            return tuple.getFirst().get(1, Long.class);
        }
        return 0L;
    }

    /**
     * Method for search events by title,text,short info and tag name.
     *
     * @param pageable      {@link Pageable}
     * @param searchingText - text criteria for searching.
     * @param languageCode  {@link String}.
     *
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

    private Predicate getPredicate(Long userId, FilterEventDto filterEventDto, String title, Root<Event> eventRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (filterEventDto != null) {
            addEventTimePredicate(filterEventDto.getEventTime(), eventRoot, predicates);
            addCitiesPredicate(filterEventDto.getCities(), eventRoot, predicates);
            addStatusPredicate(filterEventDto.getStatus(), userId, eventRoot, predicates);
            addTagsPredicate(filterEventDto.getTags(), eventRoot, predicates);
            addTitlePredicate(title, eventRoot, predicates);
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

    private void addStatusPredicate(EventStatus eventStatus, Long userId, Root<Event> eventRoot,
        List<Predicate> predicates) {
        if (eventStatus != null) {
            if (eventStatus == EventStatus.OPEN) {
                predicates.add(criteriaBuilder.isTrue(eventRoot.get(Event_.IS_OPEN)));
            } else if (eventStatus == EventStatus.CLOSED) {
                predicates.add(criteriaBuilder.isFalse(eventRoot.get(Event_.IS_OPEN)));
            } else if (eventStatus == EventStatus.CREATED && userId != null) {
                Join<Event, User> organizerJoin = eventRoot.join(Event_.organizer);
                predicates.add(criteriaBuilder.equal(organizerJoin.get(User_.ID), userId));
            } else if (eventStatus == EventStatus.JOINED && userId != null) {
                SetJoin<Event, User> attendersJoin = eventRoot.join(Event_.attenders);
                predicates.add(criteriaBuilder.equal(attendersJoin.get(User_.ID), userId));
            } else if (eventStatus == EventStatus.SAVED && userId != null) {
                SetJoin<Event, User> followersJoin = eventRoot.join(Event_.followers);
                predicates.add(criteriaBuilder.equal(followersJoin.get(User_.ID), userId));
            }
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

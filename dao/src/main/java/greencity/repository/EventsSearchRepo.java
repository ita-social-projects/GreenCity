package greencity.repository;

import greencity.entity.Tag;
import greencity.entity.event.Event;
import greencity.entity.localization.TagTranslation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
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
        CriteriaQuery<Event> criteriaQuery =
            criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        Predicate predicate = getPredicate(criteriaQuery, searchingText, languageCode, root);

        criteriaQuery.select(root).distinct(true).where(predicate);
        TypedQuery<Event> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Event> resultList = typedQuery.getResultList();

        return new PageImpl<>(resultList, pageable, getEventsCount(predicate));
    }

    private long getEventsCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Event> countEventsRoot = countQuery.from(Event.class);
        countQuery.select(criteriaBuilder.count(countEventsRoot)).where(predicate);
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

    private Predicate formTagTranslationsPredicate(CriteriaQuery<Event> criteriaQuery, String searchingText,
        String languageCode, Root<Event> root) {
        Subquery<Tag> tagSubquery = criteriaQuery.subquery(Tag.class);
        Root<Tag> tagRoot = tagSubquery.from(Tag.class);
        Join<Event, Tag> eventsTagJoin = tagRoot.join("events");

        Subquery<TagTranslation> tagTranslationSubquery = criteriaQuery.subquery(TagTranslation.class);
        Root<Tag> tagTranslationRoot = tagTranslationSubquery.correlate(tagRoot);

        Join<TagTranslation, Tag> tagTranslationTagJoin = tagTranslationRoot.join("tagTranslations");

        Predicate predicate = predicateForTags(searchingText, languageCode, tagTranslationTagJoin);
        tagTranslationSubquery.select(tagTranslationTagJoin.get("name"))
            .where(predicate);

        tagSubquery.select(eventsTagJoin).where(criteriaBuilder.exists(tagTranslationSubquery));
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

    private Predicate getPredicate(CriteriaQuery<Event> criteriaQuery, String searchingText,
        String languageCode, Root<Event> root) {
        List<Predicate> predicateList = formEventsLikePredicate(searchingText, root);
        predicateList.add(formTagTranslationsPredicate(criteriaQuery, searchingText, languageCode, root));
        return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
    }
}

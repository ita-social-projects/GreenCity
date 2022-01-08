package greencity.repository;

import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class EcoNewsSearchRepo {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public EcoNewsSearchRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<EcoNews> find(Pageable pageable, String searchingText,String languageCode) {
        CriteriaQuery<EcoNews> criteriaQuery =
                criteriaBuilder.createQuery(EcoNews.class);
        Root<EcoNews> root = criteriaQuery.from(EcoNews.class);


        /*List<Predicate> predicateList = formEcoNewsLikePredicate(searchingText,root);*/
        Predicate predicate = getPredicate(criteriaQuery,searchingText,languageCode,root);
        /*for (Predicate predicate : predicateList) {*/
            criteriaQuery.select(root).distinct(true).where(predicate);
            TypedQuery<EcoNews> typedQuery =  entityManager.createQuery(criteriaQuery);
            typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            typedQuery.setMaxResults(pageable.getPageSize());
        List<EcoNews> resultList = new ArrayList<>(typedQuery.getResultList());
       /* }*/
        long totalAmountOfEcoNews = getEcoNewsCount(predicate);
        return new PageImpl<>(resultList, pageable, totalAmountOfEcoNews);
    }

    private long getEcoNewsCount(Predicate predicate) {
        /*AtomicLong count = new AtomicLong();
        predicateList.forEach(predicate -> {*/
            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            Root<EcoNews> countEcoNewsRoot = countQuery.from(EcoNews.class);
            countQuery.select(criteriaBuilder.count(countEcoNewsRoot)).where(predicate);
          /*  count.addAndGet(entityManager.createQuery(countQuery).getSingleResult());
        });*/
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Predicate> formEcoNewsLikePredicate(String searcingText, Root<EcoNews> root) {
        Expression<String> title = root.get("title").as(String.class);
        Expression<String> text = root.get("text").as(String.class);
        Expression<String> shortInfo = root.get("shortInfo").as(String.class);

        List<Predicate> predicateList = new ArrayList<>();
        List<String> somth = new ArrayList<>(Arrays.asList(searcingText.split( " ")));
        somth.forEach(smtgtext -> predicateList.add(
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(title), "%" + smtgtext.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(text), "%" + smtgtext.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(shortInfo), "%" + smtgtext.toLowerCase() + "%"))
                        ));
        return predicateList;
    }

    private Predicate formTagTranslationsPredicate(CriteriaQuery<EcoNews> criteriaQuery,String searchingText,
                                                   String languageCode,Root<EcoNews> root) {
        Subquery<Tag> tagSubquery = criteriaQuery.subquery(Tag.class);
        Root<Tag> tagRoot = tagSubquery.from(Tag.class);
        Join<EcoNews, Tag> ecoNewsTagJoin = tagRoot.join("ecoNews");

        Subquery<TagTranslation> tagTranslationSubquery = criteriaQuery.subquery(TagTranslation.class);
        Root<Tag> tagTranslationRoot = tagTranslationSubquery.correlate(tagRoot);

        Join<TagTranslation, Tag> tagTranslationTagJoin = tagTranslationRoot.join("tagTranslations");

        Predicate predicate = predicateForTags(searchingText,languageCode,tagTranslationTagJoin);
                tagTranslationSubquery.select(tagTranslationTagJoin.get("name"))
                        .where(predicate);

        tagSubquery.select(ecoNewsTagJoin).where(criteriaBuilder.exists(tagTranslationSubquery));
        return criteriaBuilder.in(root.get("id")).value(tagSubquery);
    }

    private Predicate predicateForTags(String searchingText,String languageCode,Join<TagTranslation,Tag>  tagTranslationTagJoin){
    List<Predicate> predicateList = new ArrayList<>();
        Arrays.stream(searchingText.split(" ")).forEach(string -> {
            predicateList.add(criteriaBuilder.and(
                    criteriaBuilder.like(criteriaBuilder.lower(tagTranslationTagJoin.get("name")),
                            "%" + string.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(tagTranslationTagJoin.get("language").get("code")),
                            "%" + languageCode.toLowerCase() + "%")));
        });
        return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
    }

    private Predicate getPredicate(CriteriaQuery<EcoNews> criteriaQuery,String searchingText,
                                   String languageCode,Root<EcoNews> root){
        List<Predicate> predicateList = formEcoNewsLikePredicate(searchingText,root);
        predicateList.add(formTagTranslationsPredicate(criteriaQuery,searchingText,languageCode,root));
        return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
    }

}

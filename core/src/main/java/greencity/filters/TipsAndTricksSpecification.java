package greencity.filters;

import greencity.entity.TipsAndTricks;
import greencity.entity.TitleTranslation;
import greencity.repository.UserRepo;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Scope {@code prototype} is used for creation
 * new bean {@link TipsAndTricksSpecification} every time after new request.
 */
@Component
@Scope("prototype")
@Slf4j
@NoArgsConstructor
public class TipsAndTricksSpecification implements Specification<TipsAndTricks> {
    private transient List<SearchCriteria> searchCriteriaList;
    private transient UserRepo userRepo;

    /**
     * Constructor.
     */
    public TipsAndTricksSpecification(List<SearchCriteria> searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    /**
     * Autowire {@link UserRepo}.
     */
    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Predicate toPredicate(Root<TipsAndTricks> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("id")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getNumericPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("title")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates,
                                getTitleTranslationPredicate(root, criteriaQuery, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("author")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getAuthorPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("dateRange")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates,
                                getDataRangePredicate(root, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicates;
    }

    private Predicate getTitleTranslationPredicate(Root<TipsAndTricks> root, CriteriaQuery<?> criteriaQuery,
                                                   CriteriaBuilder criteriaBuilder, SearchCriteria searchCriteria) {
        Root<TitleTranslation> titleTranslationRoot = criteriaQuery.from(TitleTranslation.class);
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.and(criteriaBuilder.like(titleTranslationRoot.get("content"),
                "%" + searchCriteria.getValue() + "%"),
                criteriaBuilder.equal(titleTranslationRoot.get("tipsAndTricks").get("id"), root.get("id")));
    }

    private Predicate getAuthorPredicate(Root<TipsAndTricks> root, CriteriaBuilder criteriaBuilder,
                                         SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(root.get(searchCriteria.getKey()).get("name"),
                "%" + searchCriteria.getValue() + "%");
    }

    private Predicate getNumericPredicate(Root<TipsAndTricks> root, CriteriaBuilder criteriaBuilder,
                                          SearchCriteria searchCriteria) {
        try {
            return criteriaBuilder
                    .equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
        } catch (NumberFormatException ex) {
            return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction() :
                    criteriaBuilder.disjunction();
        }
    }

    private Predicate getStringPredicate(Root<TipsAndTricks> root, CriteriaBuilder criteriaBuilder,
                                         SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(root.get(searchCriteria.getKey()),
                "%" + searchCriteria.getValue() + "%");
    }

    private Predicate getDataRangePredicate(Root<TipsAndTricks> root, CriteriaBuilder criteriaBuilder,
                                            SearchCriteria searchCriteria) {
        try {
            String[] dates = (String[]) searchCriteria.getValue();
            LocalDate date1 = LocalDate.parse(dates[0]);
            LocalDate date2 = LocalDate.parse(dates[1]);
            ZonedDateTime zdt1 = date1.atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime zdt2 = date2.atStartOfDay(ZoneOffset.UTC);
            return criteriaBuilder.between(root.get(searchCriteria.getKey()), zdt1, zdt2);
        } catch (DateTimeParseException ex) {
            return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction() :
                    criteriaBuilder.disjunction();
        }
    }
}

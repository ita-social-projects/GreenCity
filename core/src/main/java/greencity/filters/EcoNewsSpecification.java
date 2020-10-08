package greencity.filters;

import greencity.entity.EcoNews;
import greencity.repository.UserRepo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Scope {@code prototype} is used for creation
 * new bean {@link EcoNewsSpecification} every time after new request.
 */
@Component
@Scope("prototype")
@Slf4j
@NoArgsConstructor
public class EcoNewsSpecification implements Specification<EcoNews> {
    private transient List<SearchCriteria> searchCriteriaList;
    private transient UserRepo userRepo;

    /**
     * Constructor.
     */
    public EcoNewsSpecification(List<SearchCriteria> searchCriteriaList) {
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
    public Predicate toPredicate(Root<EcoNews> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("id")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getNumericPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("title") || searchCriteria.getType().equals("text")
                    || searchCriteria.getType().equals("imagePath") || searchCriteria.getType().equals("source")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getStringPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("author")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getAuthorPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("dateRange")) {
                allPredicates = criteriaBuilder
                        .and(allPredicates, getDataRangePredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("tags")) {
                allPredicates =
                        criteriaBuilder.and(allPredicates, getTagsPredicate(root, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicates;
    }

    private Predicate getNumericPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
                                          SearchCriteria searchCriteria) {
        try {
            return criteriaBuilder
                    .equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
        } catch (NumberFormatException ex) {
            return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction() :
                    criteriaBuilder.disjunction();
        }
    }

    private Predicate getAuthorPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
                                         SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(root.get(searchCriteria.getKey()).get("name"),
                "%" + searchCriteria.getValue() + "%");
    }

    private Predicate getTagsPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
                                         SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(root.join("tags").get("name").as(String.class),
                "%" + searchCriteria.getValue() + "%");
    }

    private Predicate getStringPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
                                         SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(root.get(searchCriteria.getKey()),
                "%" + searchCriteria.getValue() + "%");
    }

    private Predicate getDataRangePredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
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

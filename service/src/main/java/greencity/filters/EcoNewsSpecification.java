package greencity.filters;

import greencity.entity.EcoNews;
import greencity.entity.EcoNews_;
import greencity.entity.Tag_;
import greencity.entity.localization.TagTranslation_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Scope {@code prototype} is used for creation new bean
 * {@link EcoNewsSpecification} every time after new request.
 */
@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class EcoNewsSpecification implements MySpecification<EcoNews> {
    private final transient List<SearchCriteria> searchCriteriaList;

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
            if (searchCriteria.getType().equals("creationDate")) {
                allPredicates = criteriaBuilder.and(allPredicates, getCreationDatePredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("tags")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getTagsPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("hidden")) {
                allPredicates = criteriaBuilder.and(allPredicates, getBooleanPredicate(root, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicates;
    }

    private Predicate getTagsPredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        String[] tags = searchCriteria.getValue().toString().split(",");
        List<Predicate> tagPredicates = new ArrayList<>();
        for (String tag : tags) {
            tagPredicates.add(
                    criteriaBuilder.like(
                            root.join(EcoNews_.tags).join(Tag_.tagTranslations).get(TagTranslation_.name)
                                    .as(String.class),"%" + tag.trim() + "%"));
        }
        return criteriaBuilder.and(tagPredicates.toArray(new Predicate[0]));
    }

    private Predicate getCreationDatePredicate(Root<EcoNews> root, CriteriaBuilder criteriaBuilder,
                                               SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        try {
            String date = (String) searchCriteria.getValue();
            LocalDate localDate = LocalDate.parse(date);
            ZonedDateTime zdt1 = localDate.atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime zdt2 = ZonedDateTime.of(LocalDateTime.of(localDate, LocalTime.MAX), ZoneOffset.UTC);
            return criteriaBuilder.between(root.get(searchCriteria.getKey()), zdt1, zdt2);
        } catch (DateTimeParseException ex) {
            return searchCriteria.getValue().toString().trim().isEmpty() ? criteriaBuilder.conjunction()
                    : criteriaBuilder.disjunction();
        }
    }
}

package greencity.filters;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public interface MySpecification<T> extends Specification<T> {
    /**
     * Used for build predicate for data range filter.
     */
    default Predicate getDataRangePredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        try {
            String[] dates = (String[]) searchCriteria.getValue();
            LocalDate date1 = LocalDate.parse(dates[0]);
            LocalDate date2 = LocalDate.parse(dates[1]);
            ZonedDateTime zdt1 = date1.atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime zdt2 = date2.atStartOfDay(ZoneOffset.UTC);
            return criteriaBuilder.between(root.get(searchCriteria.getKey()), zdt1, zdt2);
        } catch (DateTimeParseException ex) {
            return searchCriteria.getValue().toString().trim().isEmpty() ? criteriaBuilder.conjunction()
                : criteriaBuilder.disjunction();
        }
    }

    /**
     * Used for build predicate for numeric filter.
     */
    default Predicate getNumericPredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        try {
            return criteriaBuilder
                .equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
        } catch (NumberFormatException ex) {
            return searchCriteria.getValue().toString().trim().isEmpty() ? criteriaBuilder.conjunction()
                : criteriaBuilder.disjunction();
        }
    }

    /**
     * Used for build predicate for string filter.
     */
    default Predicate getStringPredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        return searchCriteria.getValue().toString().trim().isEmpty() ? criteriaBuilder.conjunction()
            : criteriaBuilder.like(root.get(searchCriteria.getKey()),
                "%" + searchCriteria.getValue() + "%");
    }

    /**
     * Used to build predicate for Enum filter.
     */
    default Predicate getEnumPredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        return searchCriteria.getValue().toString().trim().isEmpty() ? criteriaBuilder.conjunction()
            : criteriaBuilder.like(root.get(searchCriteria.getKey()).as(String.class),
                "%" + searchCriteria.getValue() + "%");
    }

    /**
     * Used for build predicate for author filter.
     */
    default Predicate getAuthorPredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        return searchCriteria.getValue().toString().trim().isEmpty() ? criteriaBuilder.conjunction()
            : criteriaBuilder.like(root.get(searchCriteria.getKey()).get("name"),
                "%" + searchCriteria.getValue() + "%");
    }

    default Predicate getBooleanPredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        String value = searchCriteria.getValue().toString().trim();
        return value.isEmpty() ? criteriaBuilder.conjunction()
            : criteriaBuilder.equal(root.get(searchCriteria.getKey()), Boolean.parseBoolean(value));
    }

    /**
     * Builds predicate from list of {@link SearchCriteria} using predicates
     * mapping.
     */
    default Predicate toPredicateFromMap(Root<T> root,
        CriteriaBuilder criteriaBuilder,
        List<SearchCriteria> searchCriteriaList,
        Map<String, TriFunction<Root<T>, CriteriaBuilder, SearchCriteria, Predicate>> predicatesMapping) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            TriFunction<Root<T>, CriteriaBuilder, SearchCriteria, Predicate> predicateCreator =
                predicatesMapping.get(searchCriteria.getType());
            if (predicateCreator != null) {
                Predicate predicate = predicateCreator.apply(root, criteriaBuilder, searchCriteria);
                allPredicates = criteriaBuilder.and(allPredicates, predicate);
            }
        }
        return allPredicates;
    }
}

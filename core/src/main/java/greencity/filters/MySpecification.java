package greencity.filters;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
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
            return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction() :
                criteriaBuilder.disjunction();
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
            return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction() :
                criteriaBuilder.disjunction();
        }
    }

    /**
     * Used for build predicate for string filter.
     */
    default Predicate getStringPredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
                                         SearchCriteria searchCriteria) {
        if (searchCriteria.getValue().toString().trim().equals("")) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(root.get(searchCriteria.getKey()),
            "%" + searchCriteria.getValue() + "%");
    }
}

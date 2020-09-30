package greencity.filters;

import greencity.annotations.RatingCalculationEnum;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import greencity.service.UserService;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Setter
@Component
@Slf4j
public class RatingStatisticsSpecification implements Specification<RatingStatistics> {
    private SearchCriteria searchCriteria;
    private UserService userService;
    private Predicate allPredicates;

    /**
     * jijij.
     *
     */
    public RatingStatisticsSpecification(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Predicate toPredicate(Root<RatingStatistics> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder criteriaBuilder) {
        if (allPredicates == null) {
            allPredicates = criteriaBuilder.conjunction();
        }
        if (searchCriteria.getType().equals("id")) {
            allPredicates = criteriaBuilder.and(allPredicates,  getIdPredicate(root, criteriaBuilder));
            return allPredicates;
        }
        if (searchCriteria.getType().equals("enum")) {
            allPredicates = criteriaBuilder.and(allPredicates,  getEventNamePredicate(root, criteriaBuilder));
            return allPredicates;
        }
        if (searchCriteria.getType().equals("userId")) {
            allPredicates = criteriaBuilder.and(allPredicates,  getUserIdPredicate(root, criteriaBuilder));
            return allPredicates;
        }
        if (searchCriteria.getType().equals("userMail")) {
            allPredicates = criteriaBuilder.and(allPredicates,  getUserMailPredicate(root, criteriaBuilder));
            return allPredicates;
        }
        if (searchCriteria.getType().equals("dateRange")) {
            allPredicates = criteriaBuilder.and(allPredicates,  getDataRangePredicate(root, criteriaBuilder));
            return allPredicates;
        }
        return allPredicates;
    }

    private Predicate getIdPredicate(Root<RatingStatistics> root, CriteriaBuilder criteriaBuilder) {
        try {
            return criteriaBuilder
                .equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
        } catch (NumberFormatException ex) {
            return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction() :
                criteriaBuilder.disjunction();
        }
    }

    private Predicate getEventNamePredicate(Root<RatingStatistics> root, CriteriaBuilder criteriaBuilder) {
        List<RatingCalculationEnum> enumValues = Arrays.asList(RatingCalculationEnum.values());
        List<RatingCalculationEnum> selectedEnums = enumValues.stream()
            .filter(x -> x.toString().toLowerCase().contains(((String) searchCriteria.getValue()).toLowerCase()))
            .collect(Collectors.toList());

        Predicate predicate = criteriaBuilder.disjunction();
        for (RatingCalculationEnum ratingCalculationEnum : selectedEnums) {
            predicate = criteriaBuilder.or(predicate,
                criteriaBuilder.equal(root.get(searchCriteria.getKey()), ratingCalculationEnum));
        }
        return predicate;
    }

    private Predicate getUserIdPredicate(Root<RatingStatistics> root, CriteriaBuilder criteriaBuilder) {
        try {
            User user = userService.findById(Long.valueOf((String) searchCriteria.getValue()));
            return criteriaBuilder.equal(root.get(searchCriteria.getKey()), user);
        } catch (NumberFormatException ex) {
            return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction() :
                criteriaBuilder.disjunction();
        }
    }

    private Predicate getUserMailPredicate(Root<RatingStatistics> root, CriteriaBuilder criteriaBuilder) {
        List<User> users = userService.findAll();

        Set<String> userEmails = users.stream()
            .map(User::getEmail)
            .filter(email -> email.toLowerCase().contains(((String) searchCriteria.getValue()).toLowerCase()))
            .collect(Collectors.toSet());

        Set<User> userSet = userEmails.stream()
            .map(email -> userService.findByEmail(email))
            .collect(Collectors.toSet());

        Predicate predicate = criteriaBuilder.disjunction();
        for (User user : userSet) {
            predicate =
                criteriaBuilder.or(predicate, criteriaBuilder.equal(root.get(searchCriteria.getKey()), user));
        }
        return predicate;
    }

    private Predicate getDataRangePredicate(Root<RatingStatistics> root, CriteriaBuilder criteriaBuilder) {
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
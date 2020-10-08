package greencity.filters;

import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Slf4j
@NoArgsConstructor
public class RatingStatisticsSpecification implements MySpecification<RatingStatistics> {
    private transient List<SearchCriteria> searchCriteriaList;
    private transient UserRepo userRepo;

    /**
     * Constructor.
     */
    public RatingStatisticsSpecification(List<SearchCriteria> searchCriteriaList) {
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
    public Predicate toPredicate(Root<RatingStatistics> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder criteriaBuilder) {
        Predicate allPredicates = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("id")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getNumericPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("enum")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getEventNamePredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("userId")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getUserIdPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("userMail")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getUserMailPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("dateRange")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getDataRangePredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("pointsChanged")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getNumericPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("currentRating")) {
                allPredicates =
                    criteriaBuilder.and(allPredicates, getNumericPredicate(root, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicates;
    }

    private Predicate getEventNamePredicate(Root<RatingStatistics> root, CriteriaBuilder criteriaBuilder,
                                            SearchCriteria searchCriteria) {
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

    private Predicate getUserIdPredicate(Root<RatingStatistics> root, CriteriaBuilder criteriaBuilder,
                                         SearchCriteria searchCriteria) {
        try {
            User user = userRepo.findById(Long.valueOf((String) searchCriteria.getValue()))
                .orElseThrow(
                    () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + searchCriteria.getValue()));
            return criteriaBuilder.equal(root.get(searchCriteria.getKey()), user);
        } catch (NumberFormatException ex) {
            return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction() :
                criteriaBuilder.disjunction();
        }
    }

    private Predicate getUserMailPredicate(Root<RatingStatistics> root, CriteriaBuilder criteriaBuilder,
                                           SearchCriteria searchCriteria) {
        return criteriaBuilder.like(root.get(searchCriteria.getKey()).get("email"),
            "%" + searchCriteria.getValue() + "%");
    }
}
package greencity.filters;

import greencity.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSpecification implements MySpecification<User> {
    private final transient List<SearchCriteria> searchCriteriaList;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate allPredicate = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("query")) {
                searchCriteria.setKey("id");
                Predicate idPredicate = getNumericPredicate(root, criteriaBuilder, searchCriteria);

                searchCriteria.setKey("name");
                Predicate namePredicate = getStringPredicate(root, criteriaBuilder, searchCriteria);

                searchCriteria.setKey("email");
                Predicate emailPredicate = getStringPredicate(root, criteriaBuilder, searchCriteria);

                Predicate idOrNamePredicate = criteriaBuilder.or(idPredicate, namePredicate);
                Predicate idOrNameOrEmailPredicate = criteriaBuilder.or(idOrNamePredicate, emailPredicate);
                allPredicate = criteriaBuilder.and(allPredicate, idOrNameOrEmailPredicate);
            }
            if (searchCriteria.getType().equals("status")) {
                allPredicate =
                    criteriaBuilder.and(allPredicate, getEnumPredicate(root, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicate;
    }
}

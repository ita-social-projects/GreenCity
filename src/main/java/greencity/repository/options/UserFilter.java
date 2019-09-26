package greencity.repository.options;

import greencity.dto.filter.FilterUserDto;
import greencity.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 * The class implements {@link Specification}. Constructor
 * takes a {@code DTO} class the type of which determines the further
 * creation of a new {@link Predicate} object.
 *
 * @author Rostyslav Khasanov
 */
public class  UserFilter implements Specification<User> {
    private FilterUserDto filterUserDto;

    /**
     * The constructor takes {@link FilterUserDto} object.
     *
     * @param filterUserDto object contains fields to filter by.
     */
    public UserFilter(FilterUserDto filterUserDto) {
        this.filterUserDto = filterUserDto;
    }

    /**
     * Forms a list of {@link Predicate} based on type of the classes
     * initialized in the constructors.
     */
    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (filterUserDto != null) {
            predicates.add(hasFieldsLike(root, criteriaBuilder, filterUserDto.getSearchReg()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    /**
     * Returns a predicate where {@link User} has some values defined
     * in the incoming {@link FilterUserDto} object.
     *
     * @param r  must not be {@literal null}.
     * @param cb must not be {@literal null}.
     * @return a {@link Predicate}, may be {@literal null}.
     */
    private Predicate hasFieldsLike(Root<User> r, CriteriaBuilder cb, String reg) {
        if (filterUserDto.getSearchReg() == null) {
            return cb.conjunction();
        }
        return cb.or(
            cb.like(r.get("firstName"), reg),
            cb.like(r.get("lastName"), reg),
            cb.like(r.get("email"), reg),
            cb.like(r.get("dateOfRegistration").as(String.class), reg)
        );
    }
}

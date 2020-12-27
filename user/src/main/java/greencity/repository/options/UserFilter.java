package greencity.repository.options;

import greencity.constant.UserConstants;
import greencity.dto.filter.FilterUserDto;
import greencity.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The class implements {@link Specification}. Constructor takes a {@code DTO}
 * class the type of which determines the further creation of a new
 * {@link Predicate} object.
 *
 * @author Rostyslav Khasanov
 */
public class UserFilter implements Specification<User> {
    private final transient FilterUserDto filterUserDto;

    /**
     * The constructor takes {@link FilterUserDto} object.
     *
     * @param filterUserDto object contains fields to filter by.
     */
    public UserFilter(FilterUserDto filterUserDto) {
        this.filterUserDto = filterUserDto;
    }

    /**
     * Forms a list of {@link Predicate} based on type of the classes initialized in
     * the constructors.
     */
    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(hasFieldsLike(root, criteriaBuilder, filterUserDto.getSearchReg()));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    /**
     * Returns a predicate where {@link User} has some values defined in the
     * incoming {@link FilterUserDto} object.
     *
     * @param r  must not be {@literal null}.
     * @param cb must not be {@literal null}.
     * @return a {@link Predicate}, may be {@literal null}.
     */
    private Predicate hasFieldsLike(Root<User> r, CriteriaBuilder cb, String reg) {
        reg = replaceCriteria(reg);
        return cb.or(
            cb.like(r.get(UserConstants.NAME), reg),
            cb.like(r.get(UserConstants.EMAIL), reg),
            cb.like(r.get(UserConstants.REGISTRATION_DATE).as(String.class), reg));
    }

    /**
     * Returns a String criteria for search.
     *
     * @param criteria String for search.
     * @return String creteria not be {@literal null}.
     */
    private String replaceCriteria(String criteria) {
        criteria = Optional.ofNullable(criteria).orElseGet(() -> "");
        criteria = criteria.trim();
        criteria = criteria.replace("_", "\\_");
        criteria = criteria.replace("%", "\\%");
        criteria = criteria.replace("\\", "\\\\");
        criteria = criteria.replace("'", "\\'");
        criteria = "%" + criteria + "%";
        return criteria;
    }
}

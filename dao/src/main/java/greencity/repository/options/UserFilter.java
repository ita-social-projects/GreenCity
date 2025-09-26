package greencity.repository.options;

import greencity.constant.RepoConstants;
import greencity.dto.user.UserFilterDto;
import greencity.entity.User;
import static greencity.repository.options.CriteriaUtils.replaceCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * The class implements {@link Specification}. Constructor takes a {@code DTO}
 * class the type of which determines the further creation of a new
 * {@link Predicate} object.
 *
 * @author Rostyslav Khasanov
 */
@RequiredArgsConstructor
public class UserFilter implements Specification<User> {
    private final transient UserFilterDto filterUserDto;

    /**
     * Forms a list of {@link Predicate} based on type of the classes initialized in
     * the constructors.
     */
    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (filterUserDto != null) {
            predicates.add(hasFieldsLike(root, criteriaBuilder, filterUserDto.getQuery()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
    }

    /**
     * Returns a predicate where {@link User} has some values defined in the
     * incoming {@link UserFilterDto} object.
     *
     * @param r  must not be {@literal null}.
     * @param cb must not be {@literal null}.
     * @return a {@link Predicate}, may be {@literal null}.
     */
    private Predicate hasFieldsLike(Root<User> r, CriteriaBuilder cb, String reg) {
        reg = replaceCriteria(reg);
        return cb.or(
            cb.like(r.get(RepoConstants.NAME), reg));
    }
}

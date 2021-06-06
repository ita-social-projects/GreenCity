package greencity.repository.options;

import greencity.dto.filter.FilterHabitDto;
import greencity.entity.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The class implements {@link Specification}. Constructor takes a {@code DTO}
 * class the type of which determines the further creation of a new
 * {@link Predicate} object.
 *
 * @author Hutei Volodymyr
 */
public class HabitFilter implements Specification<Habit> {
    private final transient FilterHabitDto filterHabitDto;

    /**
     * The constructor takes {@link FilterHabitDto} object.
     *
     * @param filterHabitDto object contains fields to filter by.
     */
    public HabitFilter(FilterHabitDto filterHabitDto) {
        this.filterHabitDto = filterHabitDto;
    }

    /**
     * Forms a list of {@link Predicate} based on type of the classes initialized in
     * the constructors.
     */
    @Override
    public Predicate toPredicate(Root<Habit> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (filterHabitDto != null) {
            predicates.add(hasFieldsLike(root, criteriaBuilder, filterHabitDto.getSearchReg()));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
    }

    /**
     * Returns a predicate where {@link Habit} has some values defined in the
     * incoming {@link FilterHabitDto} object.
     *
     * @param r  must not be {@literal null}.
     * @param cb must not be {@literal null}.
     * @return a {@link Predicate}, may be {@literal null}.
     */
    private Predicate hasFieldsLike(Root<Habit> r, CriteriaBuilder cb, String reg) {
        reg = replaceCriteria(reg);
//        Join join = r.join(, JoinType.INNER);
        Join<Habit, HabitTranslation> habitTranslations = r.join(Habit_.habitTranslations, JoinType.INNER);

//        CriteriaQuery<Pet> cq = cb.createQuery(Pet.class);
//        Metamodel m = em.getMetamodel();
//        EntityType<Pet> Pet_ = m.entity(Pet.class);
//        EntityType<Owner> Owner_ = m.entity(Owner.class);
//
//        Root<Pet> pet = cq.from(Pet.class);
//        Join<Owner, Address> address = cq.join(Pet_.owners).join(Owner_.addresses);

        return cb.or(
                cb.like(r.get("id").as(String.class), reg),
                cb.like(r.get("defaultDuration").as(String.class), reg),
                cb.like(r.get("complexity").as(String.class), reg),
                cb.like(habitTranslations.get("description"),reg),
                cb.like(habitTranslations.get("habitItem"), reg),
                cb.like(habitTranslations.get("name"), reg));
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


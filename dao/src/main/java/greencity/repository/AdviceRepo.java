package greencity.repository;

import greencity.entity.Advice;
import greencity.entity.Habit;
import greencity.entity.localization.AdviceTranslation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface AdviceRepo extends JpaRepository<Advice, Long>, JpaSpecificationExecutor<Advice> {
    /**
     * Method finds all {@link Advice}'s and also fetches {@link AdviceTranslation} and {@link Habit}.
     *
     * @param pageable {@link Pageable}
     * @return list of {@link Advice}'s
     * @author Markiyan Derevetslyi
     */

    @Query(value = "select a from Advice a left join fetch a.translations join fetch a.habit order by a.id",
            countQuery = "select count(a) from Advice a")
    Page<Advice> findAll(Pageable pageable);

    /**
     * Method finds all {@link Advice}'s b given query.
     *
     * @param query    {@link String} - string query
     * @param pageable {@link Pageable}
     * @return list of {@link Advice}'s
     * @author Markiyan Derevetskyi
     */
    @Query(value = "select distinct a from Advice a join fetch a.translations as at "
            + "where concat(a.id, '') like lower(concat(:query, '')) "
            + "or concat(a.habit.id, '') like lower(concat(:query, '')) "
            + "or concat(at.id, '') like lower(concat(:query, '')) "
            + "or lower(at.language.code) like lower(concat('%', :query, '%')) "
            + "or lower(at.content) like lower(concat('%', :query, '%'))",
            countQuery = "select count(a) from Advice a"
    )
    Page<Advice> searchBy(Pageable pageable, String query);

    /**
     * Method finds all {@link Advice}'s by {@link Habit} id.
     *
     * @param habitId {@link Habit} instance.
     */
    List<Advice> findAllByHabitId(Long habitId);
}

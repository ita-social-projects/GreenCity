package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitFact;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitFactRepo extends JpaRepository<HabitFact, Long>,
    JpaSpecificationExecutor<HabitFact> {
    /**
     * Method finds all {@link HabitFact}'s by {@link Habit} id.
     *
     * @param habitId {@link Habit} instance.
     */
    List<HabitFact> findAllByHabitId(Long habitId);

    /**
     * Method returns {@link HabitFact} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link HabitFact}.
     */
    @Query("SELECT DISTINCT hf FROM HabitFact hf LEFT JOIN hf.translations ht "
        + "WHERE (CONCAT(hf.id,'') LIKE LOWER(CONCAT('%', :query, '%')) "
        + "OR CONCAT(hf.habit.id,'') LIKE LOWER(CONCAT('%', :query, '%'))"
        + "OR CONCAT(ht.id,'') LIKE LOWER(CONCAT('%', :query, '%'))"
        + "OR LOWER(ht.content) LIKE LOWER(CONCAT('%', :query, '%'))"
        + "OR LOWER(ht.language.code) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<HabitFact> searchBy(Pageable paging, String query);
}

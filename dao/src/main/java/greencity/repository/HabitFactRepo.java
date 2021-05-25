package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitFact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

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
     * @param filter query to search.
     * @return list of {@link HabitFact}.
     */
    @Query("SELECT DISTINCT hf FROM HabitFact hf LEFT JOIN hf.translations ht "
        + "WHERE (CONCAT(hf.id,'') LIKE LOWER(CONCAT('%', :filter, '%')) "
        + "OR CONCAT(hf.habit.id,'') LIKE LOWER(CONCAT('%', :filter, '%'))"
        + "OR CONCAT(ht.id,'') LIKE LOWER(CONCAT('%', :filter, '%'))"
        + "OR LOWER(ht.content) LIKE LOWER(CONCAT('%', :filter, '%'))"
        + "OR LOWER(ht.language.code) LIKE LOWER(CONCAT('%', :filter, '%')))")
    Page<HabitFact> searchHabitFactByFilter(Pageable paging, String filter);
}

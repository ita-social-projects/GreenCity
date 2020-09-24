package greencity.repository;

import greencity.entity.RatingStatistics;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingStatisticsRepo extends JpaRepository<RatingStatistics, Long>,
    JpaSpecificationExecutor<RatingStatistics> {
    /**
     * Scheduled method to clean records from table rating_statistics which are older than 2 years.
     *
     * @author Dovganyuk Taras
     */
    @Modifying
    @Query(nativeQuery = true,
        value = "DELETE FROM rating_statistics WHERE create_date + interval '2 year' < current_date")
    void scheduledDeleteOlderThan();

    /**
     * Find {@link RatingStatistics} by page.
     *
     * @param pageable configuration.
     * @return {@link Page}
     * @author Dovganyuk Taras
     */
    Page<RatingStatistics> findAll(Pageable pageable);

    /**
     * Find all {@link RatingStatistics}.
     *
     * @return {@link RatingStatistics};
     * @author Dovganyuk Taras
     */
    List<RatingStatistics> findAll();
}

package greencity.repository;

import greencity.entity.RatingStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingStatisticsRepo extends JpaRepository<RatingStatistics, Long>,
    JpaSpecificationExecutor<RatingStatistics> {
    /**
     * Scheduled method to clean records from table rating_statistics which are
     * older than 2 years.
     *
     * @author Dovganyuk Taras
     */
    @Modifying
    @Query(nativeQuery = true,
        value = "DELETE FROM rating_statistics WHERE create_date + interval '2 year' < current_date")
    void scheduledDeleteOlderThan();

    @Query("""
            select r from RatingStatistics r
            join fetch r.user u
            join fetch r.ratingPoints rp
        """)
    List<RatingStatistics> findAllForExport();

}

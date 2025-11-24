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

    /**
     * Retrieves all {@link RatingStatistics} entities for export.
     *
     * <p>
     * This query loads all rating statistics together with their associated
     * {@code user} and {@code ratingPoints} using {@code join fetch} to avoid the
     * N+1 problem and ensure all related data is initialized. This method is used
     * for export functionality where complete entity data is required.
     * </p>
     *
     * @return a list of fully initialized {@link RatingStatistics} entities
     */

    @Query("""
            select r from RatingStatistics r
            join fetch r.user u
            join fetch r.ratingPoints rp
        """)
    List<RatingStatistics> findAllForExport();
}
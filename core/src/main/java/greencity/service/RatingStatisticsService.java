package greencity.service;

import greencity.entity.RatingStatistics;

/**
 * Provides the interface to manage {@link RatingStatistics} entity.
 */
public interface RatingStatisticsService {
    /**
     * Method for saving proposed {@link RatingStatistics} to database.
     *
     * @param ratingStatistics - ratingStatistics entity
     * @return RatingStatistics {@link RatingStatistics}
     * @author Dovganyuk Taras
     */
    RatingStatistics save(RatingStatistics ratingStatistics);

    /**
     * Scheduled method to clean records from table rating_statistics which are older than 2 years.
     *
     * @author Dovganyuk Taras
     */
    void scheduledDeleteOldRecords();
}

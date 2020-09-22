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
}

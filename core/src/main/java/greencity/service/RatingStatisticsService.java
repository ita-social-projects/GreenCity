package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.ratingstatistics.RatingStatisticsDto;
import greencity.entity.RatingStatistics;
import java.util.List;
import org.springframework.data.domain.Pageable;

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
     * Find {@link RatingStatistics} for management by page .
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@link PageableAdvancedDto}.
     * @author Dovganyuk Taras
     */
    PageableAdvancedDto<RatingStatisticsDto> getRatingStatisticsForManagementByPage(Pageable pageable);

    /**
     * Find {@link RatingStatistics} for management.
     *
     * @return a dto of {@link PageableDto}.
     * @author Dovganyuk Taras
     */
    List<RatingStatisticsDto> getAllRatingStatistics();
}

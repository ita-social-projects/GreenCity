package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.ratingstatistics.RatingStatisticsDto;
import greencity.dto.ratingstatistics.RatingStatisticsDtoForTables;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.filters.SearchCriteria;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {@link RatingStatisticsVO}.
 */
public interface RatingStatisticsService {
    /**
     * Method for saving proposed {@link RatingStatisticsVO} to database.
     *
     * @param ratingStatistics - ratingStatistics entity
     * @return RatingStatistics {@link RatingStatisticsVO}
     * @author Dovganyuk Taras
     */
    RatingStatisticsVO save(RatingStatisticsVO ratingStatistics);

    /**
     * Find {@link RatingStatisticsVO} for management by page .
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@link PageableAdvancedDto}.
     * @author Dovganyuk Taras
     */
    PageableAdvancedDto<RatingStatisticsDtoForTables> getRatingStatisticsForManagementByPage(Pageable pageable);

    /**
     * Find {@link RatingStatisticsVO} for management.
     *
     * @return a list of {@link RatingStatisticsDto}.
     * @author Dovganyuk Taras
     */
    List<RatingStatisticsDto> getAllRatingStatistics();

    /**
     * Find {@link RatingStatisticsVO} for export to excel file.
     *
     * @return a list of {@link RatingStatisticsDto}.
     * @author Dovganyuk Taras
     */
    List<RatingStatisticsDto> getFilteredRatingStatisticsForExcel(RatingStatisticsViewDto ratingStatisticsViewDto);

    /**
     * Find {@link RatingStatisticsVO} for management.
     *
     * @return a dto of {@link PageableDto}.
     * @author Dovganyuk Taras
     */
    PageableAdvancedDto<RatingStatisticsDtoForTables> getFilteredDataForManagementByPage(
        Pageable pageable, RatingStatisticsViewDto ratingStatisticsViewDto);

    /**
     * * This method used for build {@link SearchCriteria} depends on
     * {@link RatingStatisticsViewDto}.
     *
     * @param ratingStatisticsViewDto used for receive parameters for filters from
     *                                UI.
     * @return {@link SearchCriteria}.
     */
    List<SearchCriteria> buildSearchCriteria(RatingStatisticsViewDto ratingStatisticsViewDto);
}

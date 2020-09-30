package greencity.service.impl;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingStatisticsDto;
import greencity.dto.ratingstatistics.RatingStatisticsDtoForTables;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.entity.RatingStatistics;
import greencity.filters.RatingStatisticsSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.RatingStatisticsRepo;
import greencity.service.RatingStatisticsService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class RatingStatisticsServiceImpl implements RatingStatisticsService {
    private RatingStatisticsRepo ratingStatisticsRepo;
    private final ModelMapper modelMapper;

    private PageableAdvancedDto<RatingStatisticsDtoForTables> ratingStatisticsDtoMapper(
        Page<RatingStatistics> ratingStatistics) {
        List<RatingStatisticsDto> ratingStatisticsDtos = ratingStatistics.get()
            .map(ratingStat -> modelMapper.map(ratingStat, RatingStatisticsDto.class))
            .collect(Collectors.toList());
        List<RatingStatisticsDtoForTables> ratingStatisticsDtoForTablesDtos = ratingStatisticsDtos.stream()
            .map(x -> RatingStatisticsDtoForTables.builder()
                .id(x.getId())
                .createDate(x.getCreateDate())
                .eventName(x.getRatingCalculationEnum().toString())
                .pointsChanged(x.getPointsChanged())
                .rating(x.getRating())
                .userId(x.getUser().getId())
                .userEmail(x.getUser().getEmail())
                .build())
            .collect(Collectors.toList());

        return new PageableAdvancedDto<>(
            ratingStatisticsDtoForTablesDtos,
            ratingStatistics.getTotalElements(),
            ratingStatistics.getPageable().getPageNumber(),
            ratingStatistics.getTotalPages(),
            ratingStatistics.getNumber(),
            ratingStatistics.hasPrevious(),
            ratingStatistics.hasNext(),
            ratingStatistics.isFirst(),
            ratingStatistics.isLast());
    }

    @Transactional
    @Override
    public RatingStatistics save(RatingStatistics ratingStatistics) {
        return ratingStatisticsRepo.save(ratingStatistics);
    }

    @Override
    public PageableAdvancedDto<RatingStatisticsDtoForTables> getRatingStatisticsForManagementByPage(Pageable pageable) {
        Page<RatingStatistics> ratingStatistics = ratingStatisticsRepo.findAll(pageable);
        return ratingStatisticsDtoMapper(ratingStatistics);
    }

    @Override
    public List<RatingStatisticsDto> getAllRatingStatistics() {
        return ratingStatisticsRepo.findAll().stream()
            .map(ratingStat -> modelMapper.map(ratingStat, RatingStatisticsDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<RatingStatisticsDto> getFilteredRatingStatisticsForExcel(RatingStatisticsSpecification spec) {
        return ratingStatisticsRepo.findAll(spec).stream()
            .map(ratingStat -> modelMapper.map(ratingStat, RatingStatisticsDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public PageableAdvancedDto<RatingStatisticsDtoForTables> getFilteredDataForManagementByPage(
        Pageable pageable, RatingStatisticsSpecification spec) {
        Page<RatingStatistics> ratingStatistics = ratingStatisticsRepo.findAll(spec, pageable);
        return ratingStatisticsDtoMapper(ratingStatistics);
    }

    /**
     *   * This method used for build {@link SearchCriteria} depends on {@link RatingStatisticsViewDto}.
     *
     * @param ratingStatisticsViewDto used for receive parameters for filters from UI.
     * @return {@link SearchCriteria}.
     */
    public SearchCriteria buildSearchCriteria(RatingStatisticsViewDto ratingStatisticsViewDto) {
        SearchCriteria searchCriteria = null;
        if (ratingStatisticsViewDto.getId() != null) {
            searchCriteria = SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(ratingStatisticsViewDto.getId())
                .build();
        }
        if (ratingStatisticsViewDto.getEventName() != null) {
            searchCriteria = SearchCriteria.builder()
                .key("ratingCalculationEnum")
                .type("enum")
                .value(ratingStatisticsViewDto.getEventName())
                .build();
        }
        if (ratingStatisticsViewDto.getUserId() != null) {
            searchCriteria = SearchCriteria.builder()
                .key("user")
                .type("userId")
                .value(ratingStatisticsViewDto.getUserId())
                .build();
        }
        if (ratingStatisticsViewDto.getUserEmail() != null) {
            searchCriteria = SearchCriteria.builder()
                .key("user")
                .type("userMail")
                .value(ratingStatisticsViewDto.getUserEmail())
                .build();
        }
        if (ratingStatisticsViewDto.getStartDate() != null && ratingStatisticsViewDto.getEndDate() != null) {
            searchCriteria = SearchCriteria.builder()
                .key("createDate")
                .type("dateRange")
                .value(new String[] {ratingStatisticsViewDto.getStartDate(), ratingStatisticsViewDto.getEndDate()})
                .build();
        }
        if (ratingStatisticsViewDto.getPointsChanged() != null) {
            searchCriteria = SearchCriteria.builder()
                .key("pointsChanged")
                .type("pointsChanged")
                .value(ratingStatisticsViewDto.getPointsChanged())
                .build();
        }
        if (ratingStatisticsViewDto.getCurrentRating() != null) {
            searchCriteria = SearchCriteria.builder()
                .key("rating")
                .type("currentRating")
                .value(ratingStatisticsViewDto.getCurrentRating())
                .build();
        }
        return searchCriteria;
    }
}

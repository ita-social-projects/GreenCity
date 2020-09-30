package greencity.service.impl;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingStatisticsDto;
import greencity.dto.ratingstatistics.RatingStatisticsDtoForTables;
import greencity.entity.RatingStatistics;
import greencity.filters.RatingStatisticsSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.RatingStatisticsRepo;
import greencity.service.RatingStatisticsService;
import greencity.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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
    private UserService userService;

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
    public PageableAdvancedDto<RatingStatisticsDtoForTables> getFilteredDataForManagementByPage(
        Pageable pageable, RatingStatisticsSpecification spec) {
        Page<RatingStatistics> ratingStatistics = ratingStatisticsRepo.findAll(spec, pageable);
        return ratingStatisticsDtoMapper(ratingStatistics);
    }

    /**
     * sdfsdf.
     */
    public SearchCriteria buildSearchCriteria(String id, String eventName, String userId, String userEmail,
                                              String startDate, String endDate) {
        SearchCriteria searchCriteria = null;
        if (id != null) {
            searchCriteria = SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(id)
                .build();
        }
        if (eventName != null) {
            searchCriteria = SearchCriteria.builder()
                .key("ratingCalculationEnum")
                .type("enum")
                .value(eventName)
                .build();
        }
        if (userId != null) {
            searchCriteria = SearchCriteria.builder()
                .key("user")
                .type("userId")
                .value(userId)
                .build();
        }
        if (userEmail != null) {
            searchCriteria = SearchCriteria.builder()
                .key("user")
                .type("userMail")
                .value(userEmail)
                .build();
        }
        if (startDate != null && endDate != null) {
            searchCriteria = SearchCriteria.builder()
                .key("createDate")
                .type("dateRange")
                .value(new String[] {startDate, endDate})
                .build();
        }
        return searchCriteria;
    }
}

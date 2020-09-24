package greencity.service.impl;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingStatisticsDto;
import greencity.entity.RatingStatistics;
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

    @Transactional
    @Override
    public RatingStatistics save(RatingStatistics ratingStatistics) {
        return ratingStatisticsRepo.save(ratingStatistics);
    }

    @Override
    public PageableAdvancedDto<RatingStatisticsDto> getRatingStatisticsForManagementByPage(Pageable pageable) {
        Page<RatingStatistics> ratingStatistics = ratingStatisticsRepo.findAll(pageable);
        List<RatingStatisticsDto> ratingStatisticsDtos = ratingStatistics.get()
            .map(ratingStat -> modelMapper.map(ratingStat, RatingStatisticsDto.class))
            .collect(Collectors.toList());
        return new PageableAdvancedDto<>(
            ratingStatisticsDtos,
            ratingStatistics.getTotalElements(),
            ratingStatistics.getPageable().getPageNumber(),
            ratingStatistics.getTotalPages(),
            ratingStatistics.getNumber(),
            ratingStatistics.hasPrevious(),
            ratingStatistics.hasNext(),
            ratingStatistics.isFirst(),
            ratingStatistics.isLast());
    }

    @Override
    public List<RatingStatisticsDto> getAllRatingStatistics() {
        return ratingStatisticsRepo.findAll().stream()
            .map(ratingStat -> modelMapper.map(ratingStat, RatingStatisticsDto.class))
            .collect(Collectors.toList());
    }
}

package greencity.service.impl;

import greencity.entity.RatingStatistics;
import greencity.repository.RatingStatisticsRepo;
import greencity.service.RatingStatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class RatingStatisticsServiceImpl implements RatingStatisticsService {
    private RatingStatisticsRepo ratingStatisticsRepo;

    @Transactional
    @Override
    public RatingStatistics save(RatingStatistics ratingStatistics) {
        return ratingStatisticsRepo.save(ratingStatistics);
    }
}

package greencity.service.impl;

import greencity.entity.RatingStatistics;
import greencity.repository.RatingStatisticsRepo;
import greencity.service.RatingStatisticsService;
import java.time.Period;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingStatisticsServiceImpl implements RatingStatisticsService {
    private RatingStatisticsRepo ratingStatisticsRepo;
    @Value("${period}")
    private String period;

    /**
     * Constructor.
     */
    @Autowired
    public RatingStatisticsServiceImpl(RatingStatisticsRepo ratingStatisticsRepo) {
        this.ratingStatisticsRepo = ratingStatisticsRepo;
    }

    @Transactional
    @Override
    public RatingStatistics save(RatingStatistics ratingStatistics) {
        return ratingStatisticsRepo.save(ratingStatistics);
    }

    @Transactional
    @Override
    public void scheduledDeleteOldRecords() {
        ZonedDateTime expireDate = ZonedDateTime.now().minus(Period.parse(period));
        ratingStatisticsRepo.scheduledDeleteOlderThan(expireDate);
    }
}

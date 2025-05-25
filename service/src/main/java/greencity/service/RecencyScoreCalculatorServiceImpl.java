package greencity.service;

import static greencity.constant.RecencyScoreCalculatorConstants.DAYS_IN_A_WEEK;
import static greencity.constant.RecencyScoreCalculatorConstants.DAYS_IN_A_YEAR;
import java.time.Duration;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecencyScoreCalculatorServiceImpl implements RecencyScoreCalculatorService{

    @Override
    public double calculateRecencyScore(ZonedDateTime creationDate, double minScore, double maxScore) {
        if (creationDate == null) {
            return minScore;
        }

        long daysOld = Duration.between(creationDate, ZonedDateTime.now()).toDays();

        if (daysOld <= DAYS_IN_A_WEEK) {
            return maxScore;
        } else if (daysOld >= DAYS_IN_A_YEAR) {
            return minScore;
        } else {
            double decayRate = -Math.log(0.1) / (DAYS_IN_A_YEAR - DAYS_IN_A_WEEK);
            return maxScore * Math.exp(-decayRate * (daysOld - DAYS_IN_A_WEEK));
        }
    }
}

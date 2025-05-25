package greencity.service;

import java.time.ZonedDateTime;

public interface RecencyScoreCalculatorService {
    double calculateRecencyScore(ZonedDateTime creationDate, double minScore, double maxScore);

}

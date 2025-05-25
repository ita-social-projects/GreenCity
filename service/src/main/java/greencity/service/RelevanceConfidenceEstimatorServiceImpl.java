package greencity.service;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RelevanceConfidenceEstimatorServiceImpl implements RelevanceConfidenceEstimatorService{
    @Override
    public double estimateConfidence(double tagScore, double keywordScore, double semanticScore,
                                     int contentSize, int totalMatches) {
        double[] scores = {tagScore, keywordScore, semanticScore};
        double avgScore = Arrays.stream(scores).average().orElse(0.0);

        double weightedVariance = 0.0;
        double[] weights = {0.25, 0.35, 0.4};
        for (int i = 0; i < scores.length; i++) {
            weightedVariance += weights[i] * Math.pow(scores[i] - avgScore, 2);
        }

        double consistencyScore = Math.max(0.0, 1.0 - Math.sqrt(weightedVariance) * 2.0);
        double contentSizeScore = calculateContentSizeScore(contentSize);
        double matchQualityScore = calculateMatchQualityScore(totalMatches, contentSize);
        boolean hasOutlier = Arrays.stream(scores)
            .anyMatch(score -> Math.abs(score - avgScore) > 0.5);
        double outlierPenalty = hasOutlier ? 0.8 : 1.0;
        double highScoreBonus = avgScore > 0.7 ? 1.1 : 1.0;
        double confidenceScore = (consistencyScore * 0.4 +
            contentSizeScore * 0.3 +
            matchQualityScore * 0.3) *
            outlierPenalty *
            highScoreBonus;

        if (log.isDebugEnabled()) {
            log.debug("Confidence calculation: consistencyScore={}, contentSizeScore={}, matchQualityScore={}, " +
                    "outlierPenalty={}, highScoreBonus={}, final={}",
                consistencyScore, contentSizeScore, matchQualityScore,
                outlierPenalty, highScoreBonus, confidenceScore);
        }
        return clamp(confidenceScore, 0.0, 1.0);
    }

    private double calculateContentSizeScore(int contentSize) {
        if (contentSize < 5) {
            return 0.5;
        } else if (contentSize < 15) {
            return 0.7 + (contentSize - 5) * 0.02;
        } else if (contentSize < 50) {
            return 0.9 + (contentSize - 15) * 0.00286;
        } else {
            return 1.0;
        }
    }

    private double calculateMatchQualityScore(int totalMatches, int contentSize) {
        if (totalMatches == 0) {
            return 0.3;
        } else {
            double matchRatio = Math.min(1.0, (double) totalMatches / contentSize);
            return 0.5 + 0.5 * matchRatio;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

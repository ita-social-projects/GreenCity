package greencity.service;

import static greencity.constant.RelevanceScoreCombinerConstants.*;
import org.springframework.stereotype.Service;

@Service
public class RelevanceScoreCombinerServiceImpl implements RelevanceScoreCombinerService{

    /**
     * Combines individual scores into final relevance score
     */
    public double calculateFinalScore(double tagScore, double keywordScore,
                                      double semanticScore, double recencyScore,
                                      double minScore, double maxScore) {
        double finalScore = TAG_SCORE_WEIGHT * tagScore
            + KEYWORD_SCORE_WEIGHT * keywordScore
            + SEMANTIC_SCORE_WEIGHT * semanticScore
            + RECENCY_SCORE_WEIGHT * recencyScore;

        return clamp(finalScore, minScore, maxScore);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

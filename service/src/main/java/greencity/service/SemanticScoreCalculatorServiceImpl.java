package greencity.service;

import greencity.entity.SemanticScoreResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SemanticScoreCalculatorServiceImpl implements SemanticScoreCalculatorService {
    private final StringSimilarityCalculatorService stringSimilarityCalculatorService;

    /**
     * Calculates semantic similarity score between content words and habit names
     */
    public double calculateSemanticScore(Set<String> contentWords, Set<String> habitNames,
                                         double minScore, double maxScore) {
        if (contentWords.isEmpty() || habitNames.isEmpty()) {
            return minScore;
        }

        List<String> contentList = new ArrayList<>(contentWords);
        List<String> habitList = new ArrayList<>(habitNames);
        int totalPairs = contentList.size() * habitList.size();

        if (totalPairs == 0) {
            return minScore;
        }

        SemanticScoreResult result = computeSemanticScores(contentList, habitList);
        double normalizedScore = result.totalScore() / totalPairs;

        // Penalty for weak matches
        if (result.strongMatches() == 0 && result.totalScore() > 0) {
            normalizedScore *= 0.7;
        }

        // Size weighting
        double sizeWeight = 0.7 + 0.3 * Math.min(1.0, Math.max(contentList.size(), habitList.size()) / 30.0);
        double finalScore = normalizedScore * sizeWeight;

        return clamp(finalScore, minScore, maxScore);
    }

    private SemanticScoreResult computeSemanticScores(List<String> contentList, List<String> habitList) {
        double totalScore = 0.0;
        int strongMatches = 0;

        for (String contentWord : contentList) {
            for (String habitWord : habitList) {
                double score = stringSimilarityCalculatorService.calculateWordSimilarity(contentWord, habitWord);
                totalScore += score;
                if (score > 0.7) {
                    strongMatches++;
                }
            }
        }

        return new SemanticScoreResult(totalScore, strongMatches);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

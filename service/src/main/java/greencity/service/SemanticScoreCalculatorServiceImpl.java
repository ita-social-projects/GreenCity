package greencity.service;

import greencity.entity.SemanticScoreResult;
import greencity.entity.cache.MutableSemanticResult;
import greencity.entity.cache.Pair;
import greencity.entity.cache.ScorePair;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SemanticScoreCalculatorServiceImpl implements SemanticScoreCalculatorService {
    private final StringSimilarityCalculatorService stringSimilarityCalculatorService;

    @Override
    public double calculateSemanticScore(Set<String> contentWords, Set<String> habitNames,
                                         String language, double minScore, double maxScore) {
        if (contentWords == null || habitNames == null || contentWords.isEmpty() || habitNames.isEmpty()) {
            return minScore;
        }

        List<String> contentList = new ArrayList<>(contentWords);
        List<String> habitList = new ArrayList<>(habitNames);

        List<Pair<String, String>> filteredPairs = preFilterPairs(contentList, habitList);

        if (filteredPairs.isEmpty()) {
            return minScore;
        }

        SemanticScoreResult result = computeSemanticScores(filteredPairs, language);
        double normalizedScore = result.totalScore() / (contentList.size() * habitList.size());

        if (result.strongMatches() == 0 && result.totalScore() > 0) {
            normalizedScore *= 0.7;
        }

        double sizeWeight = 0.7 + 0.3 * Math.min(1.0, Math.max(contentList.size(), habitList.size()) / 30.0);
        double finalScore = normalizedScore * sizeWeight;

        return clamp(finalScore, minScore, maxScore);
    }

    private List<Pair<String, String>> preFilterPairs(List<String> contentList, List<String> habitList) {
        List<Pair<String, String>> pairs = new ArrayList<>();
        for (String contentWord : contentList) {
            for (String habitWord : habitList) {
                if (shouldComparePair(contentWord, habitWord)) {
                    pairs.add(new Pair<>(contentWord, habitWord));
                }
            }
        }
        return pairs;
    }

    private boolean shouldComparePair(String word1, String word2) {
        int lengthDiff = Math.abs(word1.length() - word2.length());
        if (lengthDiff <= 1) return true;

        int minLen = Math.min(word1.length(), word2.length());
        if (minLen >= 3) {
            String prefix1 = word1.substring(0, 3);
            String prefix2 = word2.substring(0, 3);
            if (prefix1.equals(prefix2)) return true;
        }

        return lengthDiff <= Math.max(word1.length(), word2.length()) * 0.5;
    }

    private SemanticScoreResult computeSemanticScores(List<Pair<String, String>> pairs, String language) {
        Stream<Pair<String, String>> stream = pairs.size() > 50 ? pairs.parallelStream() : pairs.stream();
        return stream
            .map(pair -> {
                double score;
                try {
                    score =
                        stringSimilarityCalculatorService.calculateWordSimilarity(pair.first(), pair.second(), language);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return new ScorePair(score, score > 0.7 ? 1 : 0);
            })
            .collect(
                MutableSemanticResult::new,
                (acc, scorePair) -> {
                    acc.addScore(scorePair.score());
                    acc.addStrongMatch(scorePair.strongMatch());
                },
                (acc1, acc2) -> {
                    acc1.addScore(acc2.getTotalScore());
                    acc1.addStrongMatch(acc2.getStrongMatches());
                }
            ).toResult();
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
package greencity.service;

import static greencity.constant.SimilarityMetricsCalculatorConstants.RELEVANCE_THRESHOLD;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimilarityMetricsCalculatorServiceImpl implements SimilarityMetricsCalculatorService{
    private final StringSimilarityCalculatorService stringSimilarityCalculatorService;

    /**
     * Calculates Jaccard similarity between two sets
     */
    @Override
    public double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1 == null || set2 == null || set1.isEmpty() || set2.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    /**
     * Calculates keyword overlap ratio
     */
    @Override
    public double calculateKeywordOverlap(Set<String> contentWords, Set<String> habitNames) {
        if (contentWords == null || habitNames == null || contentWords.isEmpty() || habitNames.isEmpty()) {
            return 0.0;
        }
        long matches = contentWords.stream()
            .filter(habitNames::contains)
            .count();
        return (double) matches / Math.max(contentWords.size(), habitNames.size());
    }

    /**
     * Counts similar matches between two sets using string similarity
     */
    @Override
    public int countMatches(Set<String> set1, Set<String> set2, String language)
        throws ExecutionException, InterruptedException {
        if (set1 == null || set2 == null) {
            return 0;
        }
        int matches = 0;
        for (String item1 : set1) {
            for (String item2 : set2) {
                if (item1.equals(item2) ||
                    stringSimilarityCalculatorService.calculateWordSimilarity(item1, item2, language)
                        > RELEVANCE_THRESHOLD) {
                    matches++;
                    break;
                }
            }
        }
        return matches;
    }
}

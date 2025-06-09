package greencity.service;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface SimilarityMetricsCalculatorService {
    double calculateJaccardSimilarity(Set<String> set1, Set<String> set2);
    double calculateKeywordOverlap(Set<String> contentWords, Set<String> habitNames);
    int countMatches(Set<String> set1, Set<String> set2, String language)
        throws ExecutionException, InterruptedException;
}

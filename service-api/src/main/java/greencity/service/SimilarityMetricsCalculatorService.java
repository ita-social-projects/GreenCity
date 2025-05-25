package greencity.service;

import java.util.Set;

public interface SimilarityMetricsCalculatorService {
    double calculateJaccardSimilarity(Set<String> set1, Set<String> set2);
    double calculateKeywordOverlap(Set<String> contentWords, Set<String> habitNames);
    int countMatches(Set<String> set1, Set<String> set2);
}

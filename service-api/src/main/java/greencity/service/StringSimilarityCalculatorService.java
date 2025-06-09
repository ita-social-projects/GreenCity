package greencity.service;

import java.util.concurrent.ExecutionException;

public interface StringSimilarityCalculatorService {
    double calculateWordSimilarity(String word1, String word2, String language)
        throws ExecutionException, InterruptedException;
}

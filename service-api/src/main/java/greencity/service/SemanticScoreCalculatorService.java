package greencity.service;

import java.util.Set;

public interface SemanticScoreCalculatorService {
    double calculateSemanticScore(Set<String> contentWords, Set<String> habitNames, double minScore, double maxScore);

}

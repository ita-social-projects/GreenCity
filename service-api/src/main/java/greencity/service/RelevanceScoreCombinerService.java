package greencity.service;

public interface RelevanceScoreCombinerService {
    double calculateFinalScore(double tagScore, double keywordScore,
                               double semanticScore, double recencyScore,
                               double minScore, double maxScore);
}

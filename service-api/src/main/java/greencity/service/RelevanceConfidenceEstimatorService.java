package greencity.service;

public interface RelevanceConfidenceEstimatorService {
    double estimateConfidence(double tagScore, double keywordScore, double semanticScore, int contentSize, int totalMatches);

}

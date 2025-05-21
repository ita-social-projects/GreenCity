package greencity.enums;


import static greencity.constant.AIEcoNewsRelevanceConstants.*;
import lombok.Getter;

@Getter
public enum RelevanceLevel {
    HIGH(RELEVANCE_THRESHOLD_HIGH, RELEVANCE_HIGH),
    MEDIUM_HIGH(RELEVANCE_THRESHOLD_MEDIUM_HIGH, RELEVANCE_MEDIUM_HIGH),
    MEDIUM(RELEVANCE_THRESHOLD_MEDIUM, RELEVANCE_MEDIUM),
    LOW_MEDIUM(RELEVANCE_THRESHOLD_LOW_MEDIUM, RELEVANCE_LOW_MEDIUM),
    LOW(RELEVANCE_THRESHOLD_LOW, RELEVANCE_LOW);

    private final double threshold;
    private final String description;

    RelevanceLevel(double threshold, String description) {
        this.threshold = threshold;
        this.description = description;
    }

    public static RelevanceLevel fromScore(double score) {
        for (RelevanceLevel level : RelevanceLevel.values()) {
            if (score >= level.threshold) {
                return level;
            }
        }
        return LOW;
    }
}
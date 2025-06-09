package greencity.entity.cache;

import greencity.entity.SemanticScoreResult;
import lombok.Getter;

@Getter
public class MutableSemanticResult {
    private double totalScore = 0.0;
    private int strongMatches = 0;

    public void addScore(double score) {
        this.totalScore += score;
    }

    public void addStrongMatch(int strongMatch) {
        this.strongMatches += strongMatch;
    }

    public SemanticScoreResult toResult() {
        return new SemanticScoreResult(totalScore, strongMatches);
    }
}

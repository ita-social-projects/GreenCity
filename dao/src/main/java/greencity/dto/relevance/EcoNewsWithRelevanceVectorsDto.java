package greencity.dto.relevance;

import greencity.dto.cache.CachedUserRelevanceProfile;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsRelevance;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;

@Getter
public class EcoNewsWithRelevanceVectorsDto extends EntityWithTagsVector {
    private final EcoNews ecoNews;
    private Float[] titleVector;
    private double relevanceScore;

    public EcoNewsWithRelevanceVectorsDto(EcoNews ecoNews,
                                          EcoNewsRelevance ecoNewsRelevance,
                                          Map<Long, Integer> tagsIndexes) {
        super(ecoNews.getTags(), tagsIndexes);
        this.ecoNews = ecoNews;
        if (ecoNewsRelevance != null) {
            this.titleVector = ecoNewsRelevance.getTitleVector();
        }
    }

    public EcoNewsWithRelevanceVectorsDto(EcoNews ecoNews,
                                          EcoNewsRelevance ecoNewsRelevance,
                                          Map<Long, Integer> tagsIndexes,
                                          CachedUserRelevanceProfile userProfile,
                                          double[] relevanceScoresWeights) {
        this(ecoNews, ecoNewsRelevance, tagsIndexes);
        boolean isTagsVectorInvalid = tagsVector == null
            || Arrays.stream(tagsVector).allMatch(value -> value == 0.0);
        boolean isTitleVectorInvalid = titleVector == null;
        double tagsRelevance = isTagsVectorInvalid ? 0.0 : relevanceScoresWeights[0]
            * getCosineSimilarity(tagsVector, userProfile.tagsPreferencesVector());
        double titleRelevance = isTitleVectorInvalid ? 0.0 : relevanceScoresWeights[1]
            * getCosineSimilarity(titleVector, userProfile.titlePreferencesVector());
        this.relevanceScore = tagsRelevance + titleRelevance;
    }

    private double getCosineSimilarity(Float[] vector1, Float[] vector2) {
        if (vector1 == null || vector2 == null || vector1.length != vector2.length) {
            return 0.0;
        }

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        boolean hasValidValues = false;

        for (int i = 0; i < vector1.length; i++) {
            Float a = vector1[i];
            Float b = vector2[i];

            if (a == null || b == null) {
                continue;
            }

            dot += a * b;
            normA += a * a;
            normB += b * b;
            hasValidValues = true;
        }

        if (!hasValidValues || normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

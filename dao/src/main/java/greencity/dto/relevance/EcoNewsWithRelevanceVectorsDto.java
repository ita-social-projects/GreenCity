package greencity.dto.relevance;

import greencity.dto.cache.CachedUserRelevanceProfile;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsRelevance;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;

/**
 * An {@link EcoNews} wrapper class with vectors that represents its content.
 * It contains the news itself, tags vector and title vector if available.
 * It also may contain the relevance score which is used for sorting the news by relevance.
 *
 * @author Rostyslav Zadyraichuk
 */
@Getter
public class EcoNewsWithRelevanceVectorsDto extends EntityWithTagsVector {
    private final EcoNews ecoNews;
    private Float[] titleVector;
    private double relevanceScore;

    /**
     * Computes the tags vector for the given ecoNews argument and assigns the title
     * vector if available.
     *
     * @param ecoNews          the eco news entity
     * @param ecoNewsRelevance the eco news relevance entity if present
     * @param tagsIndexes      a map that associates tag IDs with their respective
     *                         positions in the vector
     */
    public EcoNewsWithRelevanceVectorsDto(EcoNews ecoNews,
                                          EcoNewsRelevance ecoNewsRelevance,
                                          Map<Long, Integer> tagsIndexes) {
        super(ecoNews.getTags(), tagsIndexes);
        this.ecoNews = ecoNews;
        if (ecoNewsRelevance != null) {
            this.titleVector = ecoNewsRelevance.getTitleVector();
        }
    }

    /**
     * Computes the tags vector for the given ecoNews argument and assigns the title
     * vector if available. Calculates the relevance score based on the provided
     * user preference profile and weights (weights aren't rearrangeable that means
     * if either tags or title vector isn't present, news will be considered less
     * relevant).
     *
     * @param ecoNews                the eco news entity
     * @param ecoNewsRelevance       the eco news relevance entity if present
     * @param tagsIndexes            a map that associates tag IDs with their
     *                               respective positions in the vector
     * @param userProfile            the user preference profile
     * @param relevanceScoresWeights the weights of tags and title relevance scores
     */
    public EcoNewsWithRelevanceVectorsDto(EcoNews ecoNews,
                                          EcoNewsRelevance ecoNewsRelevance,
                                          Map<Long, Integer> tagsIndexes,
                                          CachedUserRelevanceProfile userProfile,
                                          double[] relevanceScoresWeights) {
        this(ecoNews, ecoNewsRelevance, tagsIndexes);
        boolean isTagsVectorInvalid = Arrays.stream(tagsVector).allMatch(value -> value == 0.0);
        boolean isTitleVectorInvalid = titleVector == null;
        double tagsRelevance = isTagsVectorInvalid ? 0.0
            : relevanceScoresWeights[0]
            * getCosineSimilarity(tagsVector, userProfile.tagsPreferencesVector());
        double titleRelevance = isTitleVectorInvalid ? 0.0
            : relevanceScoresWeights[1]
            * getCosineSimilarity(titleVector, userProfile.titlePreferencesVector());
        this.relevanceScore = tagsRelevance + titleRelevance;
    }

    private double getCosineSimilarity(Float[] vector1, Float[] vector2) {
        if (vector1.length != vector2.length) {
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

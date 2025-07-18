package greencity.dto.cache;

import java.util.Arrays;
import java.util.Objects;

/**
 * A cached profile of a user containing vectors for title and tags preferences.
 * This is used to store user preference data for efficient retrieval.
 *
 * @param tagsPreferencesVector  the user's preferences vector for tags
 * @param titlePreferencesVector the user's preferences vector for titles
 * @author Rostyslav Zadyraichuk
 */
public record CachedUserRelevanceProfile(
    Float[] tagsPreferencesVector,
    Float[] titlePreferencesVector
) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CachedUserRelevanceProfile that = (CachedUserRelevanceProfile) o;
        return Objects.deepEquals(tagsPreferencesVector, that.tagsPreferencesVector)
            && Objects.deepEquals(titlePreferencesVector, that.titlePreferencesVector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(tagsPreferencesVector), Arrays.hashCode(titlePreferencesVector));
    }

    @Override
    public String toString() {
        return "CachedUserRelevanceProfile{"
            + "tagsPreferencesVector=" + Arrays.toString(tagsPreferencesVector)
            + ", titlePreferencesVector=" + Arrays.toString(titlePreferencesVector)
            + '}';
    }
}

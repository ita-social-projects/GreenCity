package greencity.cache;

/**
 * A cached profile of a user containing vectors for title and tags preferences.
 * This is used to store user preference data for efficient retrieval.
 *
 * @param tagsPreferencesVector  the user's preferences vector for tags
 * @param titlePreferencesVector the user's preferences vector for titles
 * @author Rostyslav Zadyraichuk
 */
public record CachedUserProfile(
    Float[] tagsPreferencesVector,
    Float[] titlePreferencesVector
) {
}

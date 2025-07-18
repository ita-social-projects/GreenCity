package greencity.dto.cache;

import java.util.Map;

/**
 * Cache for EcoNews tags positions and TagsCoherence entities.
 * It is used to store and retrieve TagsCoherence by source and destination tags from cache.
 *
 * @author Rostyslav Zatyshniak
 */
public record CachedTagsWithCoherence(
    Map<Long, Integer> ecoNewsTagsIndexes,
    Map<Long, Integer> eventTagsIndexes,
    Map<Long, Integer> habitTagsIndexes,
    Map<Long, Map<Long, Float>> tagsCoherenceIds
) {
}

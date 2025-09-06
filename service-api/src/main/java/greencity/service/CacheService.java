package greencity.service;

import greencity.dto.cache.CachedTagsWithCoherence;
import greencity.dto.cache.CachedUserRelevanceProfile;
import greencity.dto.cache.CachedUserRelevantNews;
import greencity.dto.cache.RelevantEcoNewsCacheKey;

/**
 * Service for managing cache.
 *
 * @author Rostyslav Zadyraichuk
 */
public interface CacheService {
    /**
     * Get user relevant news from cache saved for specific user by request filters.
     *
     * @param key request filters and user specification
     * @return cached news identifiers and metadata
     */
    CachedUserRelevantNews getUserRelevantNewsFromCache(RelevantEcoNewsCacheKey key);

    /**
     * Get user profile from cache saved for specific user.
     *
     * @param userId user id
     * @return cached user's relevance preferences
     */
    CachedUserRelevanceProfile getUserProfileFromCache(Long userId);

    /**
     * Get tags coherence from cache.
     *
     * @return cached tags and their coherence
     */
    CachedTagsWithCoherence getTagsCoherenceFromCache();
}

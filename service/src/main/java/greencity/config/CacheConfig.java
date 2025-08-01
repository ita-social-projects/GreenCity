package greencity.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import greencity.dto.cache.CachedTagsWithCoherence;
import greencity.dto.cache.CachedUserRelevanceProfile;
import greencity.dto.cache.CachedUserRelevantNews;
import greencity.dto.cache.RelevantEcoNewsCacheKey;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.TagsCoherence;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Value("${greencity.cache.relevance.expiration.time}")
    private int expirationTimeInMinutes;

    /**
     * Caffeine cache for storing metadata and collection of {@link EcoNews}, that
     * has already been filtered by user's request, sorted by calculated relevance
     * score for specific user and returned to him.
     *
     * @return Caffeine {@link Cache} with relevant news.
     */
    @Bean
    public Cache<RelevantEcoNewsCacheKey, CachedUserRelevantNews> userRelevanceNewsCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(expirationTimeInMinutes, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();
    }

    /**
     * Caffeine cache for storing user's preference profile for news obtaining.
     * Profile contains average tags and title vectors of {@link EcoNews}.
     *
     * @return Caffeine {@link Cache} with user's relevance profile.
     */
    @Bean
    public Cache<Long, CachedUserRelevanceProfile> averageUserVectorsCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(expirationTimeInMinutes, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();
    }

    /**
     * Caffeine cache for storing all available {@link Tag}s and
     * {@link TagsCoherence}s in database. It helps to keep consistency for relevant
     * {@link EcoNews} obtaining.
     *
     * @return Caffeine {@link Cache} with tags and tags coherence.
     */
    @Bean
    public Cache<Long, CachedTagsWithCoherence> tagsCoherenceCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(expirationTimeInMinutes, TimeUnit.MINUTES)
            .maximumSize(1)
            .build();
    }
}

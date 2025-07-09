package greencity.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import greencity.cache.CachedUserProfile;
import greencity.cache.CachedUserRelevantNews;
import greencity.cache.RelevantEcoNewsCacheKey;
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

    @Bean
    public Cache<RelevantEcoNewsCacheKey, CachedUserRelevantNews> userRelevanceNewsCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(expirationTimeInMinutes, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();
    }

    @Bean
    public Cache<Long, CachedUserProfile> averageUserVectorsCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(expirationTimeInMinutes, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();
    }
}

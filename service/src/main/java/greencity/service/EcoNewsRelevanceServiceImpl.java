package greencity.service;

import com.github.benmanes.caffeine.cache.Cache;
//import greencity.cache.CachedUserRelevance;
import greencity.cache.CachedUserRelevantNews;
import greencity.cache.RelevantEcoNewsCacheKey;
import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsRelevance;
import greencity.entity.Tag;
import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.TagsCoherenceRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class EcoNewsRelevanceServiceImpl implements EcoNewsRelevanceService {
    private final EcoNewsRelevanceRepo ecoNewsRelevanceRepo;
    private final TagsCoherenceRepo tagsCoherenceRepo;
    private final Cache<RelevantEcoNewsCacheKey, CachedUserRelevantNews> userRelevanceNewsCache;
    private final Cache<Long, Float[]> averageTitleVectorCache;
    private final ModelMapper modelMapper;

    @Override
    public PageableDto<EcoNewsDto> findRelevantEcoNews(UserVO user, Pageable pageable) {
        return null;
    }

    private float countRelevanceForUserAndEcoNews(UserVO user, EcoNewsRelevance ecoNewsRelevance) {
        return 0;
    }

    private double getCosineSimilarity(Float[] vector1, Float[] vector2) {
        if (vector1 == null || vector2 == null || vector1.length != vector2.length) {
            return 0.0;
        }
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            dot += vector1[i] * vector2[i];
            normA += vector1[i] * vector1[i];
            normB += vector2[i] * vector2[i];
        }
        if (normA == 0 && normB == 0) {
            return 0.0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private Float[] averageVectors(List<Float[]> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            return null;
        }
        int dimension = vectors.get(0).length;
        Float[] averageVector = new Float[dimension];
        Arrays.fill(averageVector, 0f);

        for (Float[] vector : vectors) {
            for (int i = 0; i < dimension; i++) {
                averageVector[i] += vector[i];
            }
        }
        for (int i = 0; i < dimension; i++) {
            averageVector[i] /= dimension;
        }
        return averageVector;
    }

    private double tagRelevance(EcoNews news, Map<Tag, Double> userTagProfile) {
        List<Tag> newsTags = news.getTags();
        if (newsTags == null || newsTags.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Tag tag : newsTags) {
            sum += userTagProfile.getOrDefault(tag, 0.0);
        }
        return sum / newsTags.size();

    }

    private Float[] getAverageTitleVectorByUser(Long userId) {
        Float[] cacheValue = averageTitleVectorCache.getIfPresent(userId);
        if (cacheValue != null) {
            return cacheValue;
        }
        List<EcoNewsRelevance> lastLikedNews = ecoNewsRelevanceRepo.findLikedEcoNewsById(userId);
        List<Float[]> vectors = new ArrayList<>();
        for (EcoNewsRelevance relevance : lastLikedNews) {
            vectors.add(relevance.getTitleVector());
        }
        return averageVectors(vectors);
    }
}

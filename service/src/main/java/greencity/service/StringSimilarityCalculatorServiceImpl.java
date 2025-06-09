package greencity.service;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import static greencity.constant.StringSimilarityCalculator.*;
import static greencity.constant.UserEcoNewsRelevanceConstants.MIN_CONFIDENCE_THRESHOLD;
import greencity.entity.cache.Pair;
import greencity.model.StringLengthInfo;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StringSimilarityCalculatorServiceImpl implements StringSimilarityCalculatorService {
    private static final JaroWinklerSimilarity JARO_WINKLER = new JaroWinklerSimilarity();
    private static final Weigher<Pair<String, String>, Double> SIMILARITY_WEIGHER = (key, value) ->
        key.first().length() + key.second().length() + 8;

    private final AsyncLoadingCache<Pair<String, String>, Double> similarityCache = Caffeine.newBuilder()
        .maximumWeight(SIMILARITY_CACHE_MAX_WEIGHT)
        .weigher(SIMILARITY_WEIGHER)
        .expireAfterWrite(SIMILARITY_CACHE_EXPIRE_AFTER_WRITE_MINUTES, TimeUnit.MINUTES)
        .refreshAfterWrite(SIMILARITY_CACHE_REFRESH_AFTER_WRITE_MINUTES, TimeUnit.MINUTES)
        .recordStats()
        .buildAsync((key, executor) -> computeSimilarityAsync(key));

    @Override
    public double calculateWordSimilarity(String word1, String word2, String language) {
        Double score = validateInput(word1, word2);
        if (score != null) {
            return score;
        }
        Pair<String, String> pair = new Pair<>(word1, word2);
        try {
            score = handleCache(pair).get();
            if (score != null) {
                return score;
            }
        } catch (Exception e) {
            // Fallback to synchronous computation if async fails
            score = computeSimilaritySynchronously(word1, word2);
            similarityCache.synchronous().put(pair, score);
            return score;
        }
        StringLengthInfo lengthInfo = computeStringLengths(word1, word2);
        if (checkLengthDifference(lengthInfo.minLength(), lengthInfo.maxLength())) {
            similarityCache.synchronous().put(pair, 0.0);
            return 0.0;
        }
        score = computeSimilarityScore(lengthInfo.trimmed1(), lengthInfo.trimmed2(), lengthInfo.minLength(), lengthInfo.maxLength());
        score = applyLengthWeight(score, lengthInfo.maxLength());
        similarityCache.synchronous().put(pair, score);
        return score;
    }

    private CompletableFuture<Double> computeSimilarityAsync(Pair<String, String> key) {
        return CompletableFuture.supplyAsync(() ->
            computeSimilaritySynchronously(key.first(), key.second()));
    }

    private double computeSimilaritySynchronously(String word1, String word2) {
        Double score = validateInput(word1, word2);
        if (score != null) {
            return score;
        }
        StringLengthInfo lengthInfo = computeStringLengths(word1, word2);
        if (checkLengthDifference(lengthInfo.minLength(), lengthInfo.maxLength())) {
            return 0.0;
        }
        score = computeSimilarityScore(lengthInfo.trimmed1(), lengthInfo.trimmed2(), lengthInfo.minLength(), lengthInfo.maxLength());
        return applyLengthWeight(score, lengthInfo.maxLength());
    }

    private StringLengthInfo computeStringLengths(String word1, String word2) {
        String[] trimmed = trimStrings(word1, word2);
        String trimmed1 = trimmed[0];
        String trimmed2 = trimmed[1];
        int length1 = trimmed1.length();
        int length2 = trimmed2.length();
        return new StringLengthInfo(trimmed1, trimmed2, Math.min(length1, length2), Math.max(length1, length2));
    }

    private CompletableFuture<Double> handleCache(Pair<String, String> pair) {
        return similarityCache.get(pair);
    }

    private Double validateInput(String word1, String word2) {
        if (word1 == null || word2 == null || word1.isEmpty() || word2.isEmpty()) {
            return 0.0;
        }
        if (word1.equals(word2)) {
            return 1.0;
        }
        return null;
    }

    private String[] trimStrings(String word1, String word2) {
        String trimmed1 = word1.length() > MAX_STRING_LENGTH ? word1.substring(0, MAX_STRING_LENGTH) : word1;
        String trimmed2 = word2.length() > MAX_STRING_LENGTH ? word2.substring(0, MAX_STRING_LENGTH) : word2;
        return new String[]{trimmed1, trimmed2};
    }

    private boolean checkLengthDifference(int minLength, int maxLength) {
        return Math.abs(maxLength - minLength) > MAX_LENGTH_DIFFERENCE;
    }

    private double computeSimilarityScore(String trimmed1, String trimmed2, int minLength, int maxLength) {
        if (maxLength < SHORT_STRING_THRESHOLD) {
            return computeShortStringSimilarity(trimmed1, trimmed2);
        } else {
            return computeLongStringSimilarity(trimmed1, trimmed2, minLength, maxLength);
        }
    }

    private double computeShortStringSimilarity(String trimmed1, String trimmed2) {
        return JARO_WINKLER.apply(trimmed1, trimmed2);
    }

    private double computeLongStringSimilarity(String trimmed1, String trimmed2, int minLength, int maxLength) {
        double score = 0.0;
        double prefixSuffixScore = calculatePrefixSuffixSimilarity(trimmed1, trimmed2, minLength, maxLength);
        score = Math.max(score, prefixSuffixScore);
        int levenshteinDistance = calculateLevenshteinDistance(trimmed1, trimmed2);
        if (levenshteinDistance != -1) {
            double levenshteinSimilarity = 1.0 - (levenshteinDistance / (double) maxLength);
            if (levenshteinSimilarity > MIN_CONFIDENCE_THRESHOLD) {
                score = Math.max(score, levenshteinSimilarity * LEVENSHTEIN_WEIGHT);
            }
        }
        double ngramSimilarity = calculateNGramsSimilarity(trimmed1, trimmed2, NGRAM_SIZE);
        score = Math.max(score, ngramSimilarity * NGRAM_WEIGHT);
        return score;
    }

    private double applyLengthWeight(double score, int maxLength) {
        double lengthWeight = BASE_LENGTH_WEIGHT + Math.min(MAX_LENGTH_WEIGHT_ADJUSTMENT, maxLength / LENGTH_WEIGHT_DENOMINATOR);
        return score * lengthWeight;
    }

    private double calculatePrefixSuffixSimilarity(String word1, String word2, int minLength, int maxLength) {
        double score = 0.0;
        double lengthRatio = (double) minLength / maxLength;
        int windowSize = Math.min(PREFIX_SUFFIX_WINDOW_SIZE, minLength);
        if (windowSize > 0 && word1.regionMatches(0, word2, 0, windowSize)) {
            score = Math.max(score, PREFIX_MATCH_WEIGHT * lengthRatio);
        }
        if (windowSize > 0 && word1.regionMatches(word1.length() - windowSize, word2, word2.length() - windowSize, windowSize)) {
            score = Math.max(score, SUFFIX_MATCH_WEIGHT * lengthRatio);
        }
        return score;
    }

    private int calculateLevenshteinDistance(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return -1;
        }
        int len1 = s1.length();
        int len2 = s2.length();
        int[] costs = new int[len2 + 1];

        for (int j = 0; j <= len2; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= len1; i++) {
            int previousValue = costs[0];
            costs[0] = i;
            for (int j = 1; j <= len2; j++) {
                int newValue = costs[j - 1];
                if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                    newValue = Math.min(Math.min(newValue, previousValue), costs[j]) + 1;
                }
                previousValue = costs[j];
                costs[j] = newValue;
                if (newValue > LEVENSHTEIN_THRESHOLD) {
                    return -1;
                }
            }
        }
        return costs[len2];
    }

    @SuppressWarnings("BetaApi")
    private double calculateNGramsSimilarity(String s1, String s2, int n) {
        if (s1.length() < n || s2.length() < n) {
            return 0.0;
        }
        BloomFilter<Long> ngrams1 = BloomFilter.create(Funnels.longFunnel(), EXPECTED_NGRAMS, FALSE_POSITIVE_RATE);
        BloomFilter<Long> ngrams2 = BloomFilter.create(Funnels.longFunnel(), EXPECTED_NGRAMS, FALSE_POSITIVE_RATE);

        final int[] counters = new int[2];
        final long[] currentHash = new long[1];

        processNGrams(s1, n, ngrams1, currentHash, () -> counters[0]++);
        processNGrams(s2, n, ngrams2, currentHash, () -> {
            counters[0]++;
            if (ngrams1.mightContain(currentHash[0])) {
                counters[1]++;
            }
        });

        int unionCount = counters[0] - counters[1];
        int intersectionCount = counters[1];
        return unionCount == 0 ? 0.0 : (double) intersectionCount / unionCount;
    }

    @SuppressWarnings("BetaApi")
    private void processNGrams(String s, int n, BloomFilter<Long> filter, long[] currentHash, Runnable counter) {
        long hash = 0;
        long power = 1;
        for (int i = 0; i < n; i++) {
            power *= ROLLING_HASH_MULTIPLIER;
        }
        for (int i = 0; i <= s.length() - n; i++) {
            if (i == 0) {
                for (int j = 0; j < n; j++) {
                    hash = hash * ROLLING_HASH_MULTIPLIER + s.charAt(j);
                }
            } else {
                hash = (hash - s.charAt(i - 1) * power) * ROLLING_HASH_MULTIPLIER + s.charAt(i + n - 1);
            }
            filter.put(hash);
            currentHash[0] = hash;
            counter.run();
        }
    }
}

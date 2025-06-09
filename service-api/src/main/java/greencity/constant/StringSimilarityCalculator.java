package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringSimilarityCalculator {
    public static final int MAX_STRING_LENGTH = 50;
    public static final int MAX_LENGTH_DIFFERENCE = 20;
    public static final int SHORT_STRING_THRESHOLD = 20;
    public static final double LENGTH_WEIGHT_DENOMINATOR = 20.0;
    public static final int PREFIX_SUFFIX_WINDOW_SIZE = 5;
    public static final double PREFIX_MATCH_WEIGHT = 0.7;
    public static final double SUFFIX_MATCH_WEIGHT = 0.6;
    public static final double BASE_LENGTH_WEIGHT = 0.5;
    public static final double MAX_LENGTH_WEIGHT_ADJUSTMENT = 0.5;
    public static final double LEVENSHTEIN_WEIGHT = 0.8;
    public static final double NGRAM_WEIGHT = 0.5;
    public static final int LEVENSHTEIN_THRESHOLD = 10;
    public static final int EXPECTED_NGRAMS = 1000;
    public static final double FALSE_POSITIVE_RATE = 0.01;
    public static final int NGRAM_SIZE = 3;
    public static final int ROLLING_HASH_MULTIPLIER = 31;
    public static final long SIMILARITY_CACHE_MAX_WEIGHT = 10000;
    public static final long SIMILARITY_CACHE_EXPIRE_AFTER_WRITE_MINUTES = 60;
    public static final long SIMILARITY_CACHE_REFRESH_AFTER_WRITE_MINUTES = 30;
}

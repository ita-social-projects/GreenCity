package greencity.constant;

import java.time.Duration;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserEcoNewsRelevanceConstants {
    public static final String UNDERSCORE = "_";
    public static final String UK_LANGUAGE_CODE = "uk";
    public static final long USER_HABITS_CACHE_MAX_WEIGHT = 10000;
    public static final Duration USER_HABITS_REFRESH_INTERVAL = Duration.ofHours(1);
    public static final Duration USER_HABITS_CACHE_EXPIRY = Duration.ofHours(2);
    public static final Duration AI_NEWS_CACHE_EXPIRY = Duration.ofMinutes(15);
    public static final long AI_NEWS_CACHE_MAX_WEIGHT = 5000;
    public static final Duration AI_NEWS_REFRESH_INTERVAL = Duration.ofMinutes(10);
    public static final double MIN_CONFIDENCE_THRESHOLD = 0.6;
    public static final Pattern UKRAINIAN_PATTERN = Pattern.compile("[іїєґ]", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    public static final String EMPTY_STRING = " ";
    public static final String AI_USER_EMAIL = "ai.generated@example.com";
    public static final double RELEVANCE_THRESHOLD = 0.6;
    public static final String USER_NOT_FOUND_MESSAGE = "User not found: ";
    public static final int DAYS_IN_A_YEAR = 365;
    public static final double FALLBACK_THRESHOLD = 0.5;
    public static final int ASYNC_THRESHOLD = 100;
    public static final int LANGUAGE_INDEX_IN_CACHE_KEY = 2;
    public static final int EMPTY_ARRAY_SIZE = 0;
}

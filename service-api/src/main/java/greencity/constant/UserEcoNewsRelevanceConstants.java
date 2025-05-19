package greencity.constant;

import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserEcoNewsRelevanceConstants {
    public static final String AI_USER_EMAIL = "ai.generated@example.com";
    public static final double RELEVANCE_THRESHOLD = 0.6;
    public static final Set<String> STOP_WORDS = Set.of(
        "a", "an", "the", "and", "or", "but", "in",
        "on", "at", "to", "for", "of", "with", "by"
    );
    public static final String EMPTY_STRING = " ";
    public static final String REGEX_SPLIT_PATTERN = "\\W+";
    public static final String USER_NOT_FOUND_MESSAGE = "User not found: ";
    public static final double TAG_SCORE_WEIGHT = 0.4;
    public static final double KEYWORD_SCORE_WEIGHT = 0.4;
    public static final double RECENCY_SCORE_WEIGHT = 0.2;
    public static final double MIN_SCORE = 0.0;
    public static final double MAX_SCORE = 1.0;
    public static final int DAYS_IN_A_WEEK = 7;
    public static final int DAYS_IN_A_YEAR = 365;
    public static final int MIN_WORD_LENGTH = 2;
}

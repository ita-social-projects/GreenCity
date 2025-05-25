package greencity.constant;

import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EcoNewsContentAnalyzerConstants {
    public static final Set<String> STOP_WORDS = Set.of(
        "a", "an", "the", "and", "or", "but", "in",
        "on", "at", "to", "for", "of", "with", "by"
    );
    public static final String EMPTY_STRING = " ";
    public static final String REGEX_SPLIT_PATTERN = "\\W+";
    public static final int MIN_WORD_LENGTH = 2;
}

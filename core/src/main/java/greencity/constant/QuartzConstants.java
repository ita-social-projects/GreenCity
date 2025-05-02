package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class QuartzConstants {
    public static final String JOB_DETAILS_IDENTITY = "ecoNewsGenerationJob";
    public static final String TRIGGER_IDENTITY = "ecoNewsGenerationTrigger";

    public static final String QUESTION_MARK = "?";
    public static final String STAR = "*";
    public static final String SPACE = " ";
    public static final String SPLIT_REGEX = "\\s+";
    public static final int CRON_FIELDS_COUNT = 6;
    public static final int DAY_OF_MONTH_INDEX = 3;
    public static final int DAY_OF_WEEK_INDEX = 5;
}

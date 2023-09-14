package greencity.enums;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
public enum RatingCalculationEnum {
    ACQUIRED_HABIT_14_DAYS(20),

    ACQUIRED_HABIT_21_DAYS(30),

    ACQUIRED_HABIT_30_PLUS_DAYS(40),

    DAYS_OF_HABIT_IN_PROGRESS(1), // 1 point per 1 day

    CREATE_NEWS(20),

    ADDED_TIPS_AND_TRICKS(10),

    COMMENT_OR_REPLY(2),

    LIKE_COMMENT_OR_REPLY(10),

    SHARE_NEWS_OR_TIPS_AND_TRICKS(20),

    FIRST_5_ACHIEVEMENTS(20),

    FIRST_10_ACHIEVEMENTS(30),

    FIRST_15_ACHIEVEMENTS(40),

    FIRST_20_ACHIEVEMENTS(50),

    UNDO_ACQUIRED_HABIT_14_DAYS(-20),

    UNDO_ACQUIRED_HABIT_21_DAYS(-30),

    UNDO_ACQUIRED_HABIT_30_PLUS_DAYS(-40),

    UNDO_DAYS_OF_HABIT_IN_PROGRESS(-1),

    DELETE_NEWS(-20),

    DELETE_TIPS_AND_TRICKS(-10),

    DELETE_COMMENT_OR_REPLY(-2),

    UNLIKE_COMMENT_OR_REPLY(-10),

    UNDO_SHARE_NEWS_OR_TIPS_AND_TRICKS(-20),

    UNDO_FIRST_5_ACHIEVEMENTS(-20),

    UNDO_FIRST_10_ACHIEVEMENTS(-30),

    UNDO_FIRST_15_ACHIEVEMENTS(-40),

    UNDO_FIRST_20_ACHIEVEMENTS(-50);

    private final float ratingPoints;

    RatingCalculationEnum(float ratingPoints) {
        this.ratingPoints = ratingPoints;
    }
}

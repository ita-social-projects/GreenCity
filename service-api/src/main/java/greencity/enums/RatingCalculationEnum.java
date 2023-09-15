package greencity.enums;

import lombok.Getter;

@Getter
public enum RatingCalculationEnum {
    DAYS_OF_HABIT_IN_PROGRESS(1),

    CREATE_NEWS(20),

    COMMENT_OR_REPLY(2),

    LIKE_COMMENT_OR_REPLY(10),

    SHARE_NEWS(20),

    UNDO_DAYS_OF_HABIT_IN_PROGRESS(-1),

    DELETE_NEWS(-20),

    DELETE_COMMENT_OR_REPLY(-2),

    UNLIKE_COMMENT_OR_REPLY(-10),

    UNDO_SHARE_NEWS(-20);

    private final int ratingPoints;

    RatingCalculationEnum(int ratingPoints) {
        this.ratingPoints = ratingPoints;
    }
}

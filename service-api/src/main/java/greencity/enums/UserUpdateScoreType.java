package greencity.enums;

import lombok.Getter;

@Getter
public enum UserUpdateScoreType {
    ACQUIRED_HABIT_14_DAYS(20),
    ACQUIRED_HABIT_21_DAYS(30),
    ACQUIRED_HABIT_30_PLUS_DAYS(40),
    DAYS_OF_HABIT_IN_PROGRESS(1), // 1 point per 1 day
    CREATED_NEWS(20),
    ADDED_TIPS_AND_TRICKS(10),
    COMMENT_OR_REPLY(2),
    LIKE_COMMENT_OR_REPLY(10),
    SHARE_NEWS_OR_TIPS_AND_TRICKS(20),
    FIRST_5_ACHIEVEMENTS(20),
    FIRST_10_ACHIEVEMENTS(30),
    FIRST_15_ACHIEVEMENTS(40),
    FIRST_20_ACHIEVEMENTS(50);

    private final int points;

    UserUpdateScoreType(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}

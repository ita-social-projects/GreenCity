package greencity.enums;

import lombok.Getter;

@Getter
public enum AchievementCategory {
    ACHIEVEMENTS("Achievements"),
    ECO_NEWS("EcoNews"),
    ECO_NEWS_COMMENT("EcoNewsComment"),
    ECO_NEWS_LIKE("EcoNewsLikes"),
    TIPS_AND_TRICKS("Tips&Tricks"),
    HABIT_STREAK("HabitStreak"),
    ACQUIRED_HABIT("AcquiredHabit"),
    TIPS_AND_TRICKS_COMMENTS("Tips&TricksComments"),
    SOCIAL_NETWORK("SocialNetworks");

    private final String category;

    AchievementCategory(String category) {
        this.category = category;
    }
}

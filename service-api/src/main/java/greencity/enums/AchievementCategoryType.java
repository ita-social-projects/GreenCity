package greencity.enums;

import lombok.Getter;

@Getter
public enum AchievementCategoryType {
    ECO_NEWS("EcoNews"),
    ECO_NEWS_COMMENT("EcoNewsComments"),
    ECO_NEWS_LIKE("EcoNewsLikes"),
    ACHIEVEMENTS("Achievements"),
    TIPS_AND_TRICKS_LIKES("Tips&TricksLikes"),
    HABIT_STREAK("HabitStreak"),
    ACQUIRED_HABIT("AcquiredHabits"),
    TIPS_AND_TRICKS_COMMENTS("Tips&TricksComments"),
    SOCIAL_NETWORK("SocialNetworks");

    private final String category;

    AchievementCategoryType(String category) {
        this.category = category;
    }
}

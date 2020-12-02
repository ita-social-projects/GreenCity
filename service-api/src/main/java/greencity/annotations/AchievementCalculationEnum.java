package greencity.annotations;

import lombok.Getter;

@Getter
public enum AchievementCalculationEnum {

    ECO_NEWS("EcoNews"),
    ECO_NEWS_LIKES("EcoNewsLikes"),
    ECO_NEWS_COMMENTS("EcoNewsComments"),
    TIPS_AND_TRICKS_LIKES("Tips&TricksLikes"),
    TIPS_AND_TRICKS_COMMENTS("Tips&TricksComments"),
    ACQUIRED_HABIT("AcquiredHabit"),
    HABIT_STREAK("HabitStreak"),
    SOCIAL_NETWORKS("SocialNetworks"),
    RATING("Rating"),
    ACHIEVEMENTS("Achievements");

    private final String achievementCategory;

    AchievementCalculationEnum(String achievementCategory) {
        this.achievementCategory = achievementCategory;
    }

}

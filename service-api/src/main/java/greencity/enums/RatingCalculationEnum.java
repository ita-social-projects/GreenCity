package greencity.enums;

import lombok.Getter;

@Getter
public enum RatingCalculationEnum {
    ADD_COMMENT(2),
    DELETE_COMMENT(-2),
    LIKE_COMMENT(1),
    UNLIKE_COMMENT(-1),
    ADD_TIPS_AND_TRICKS(10),
    DELETE_TIPS_AND_TRICKS(-10),
    ADD_ECO_NEWS(20),
    DELETE_ECO_NEWS(-20);

    private final float ratingPoints;

    RatingCalculationEnum(float ratingPoints) {
        this.ratingPoints = ratingPoints;
    }
}

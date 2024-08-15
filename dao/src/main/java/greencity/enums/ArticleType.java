package greencity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ArticleType {
    EVENT("Event"),
    ECO_NEWS("Eco news"),
    HABIT("Habit");

    private final String description;
}

package greencity.dto.relevance;

import greencity.dto.cache.CachedTagsWithCoherence;
import greencity.entity.Habit;
import lombok.Getter;

@Getter
public class HabitWithTagsVectorDto extends EntityWithTagsVector {
    private final Habit habit;

    public HabitWithTagsVectorDto(Habit habit, CachedTagsWithCoherence tags) {
        super(habit.getTags(), tags.habitTagsIndexes());
        this.habit = habit;
    }
}

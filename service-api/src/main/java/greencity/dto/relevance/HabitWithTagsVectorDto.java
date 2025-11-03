package greencity.dto.relevance;

import greencity.dto.cache.CachedTagsWithCoherence;
import java.util.Collection;
import lombok.Getter;

/**
 * A habit wrapper class with vector that represents its content.
 *
 * @author Rostyslav Zadyraichuk
 */
@Getter
public class HabitWithTagsVectorDto extends EntityWithTagsVector {
    private final Long habitId;

    public HabitWithTagsVectorDto(Long habitId, Collection<Long> tagIds, CachedTagsWithCoherence tags) {
        super(tagIds, tags.habitTagsIndexes());
        this.habitId = habitId;
    }
}

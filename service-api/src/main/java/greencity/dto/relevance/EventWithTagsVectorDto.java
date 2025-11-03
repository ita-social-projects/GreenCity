package greencity.dto.relevance;

import greencity.dto.cache.CachedTagsWithCoherence;
import java.util.Collection;
import lombok.Getter;

/**
 * An event wrapper class with vector that represents its content.
 *
 * @author Rostyslav Zadyraichuk
 */
@Getter
public class EventWithTagsVectorDto extends EntityWithTagsVector {
    private final Long eventId;

    public EventWithTagsVectorDto(Long eventId, Collection<Long> tagIds, CachedTagsWithCoherence tags) {
        super(tagIds, tags.eventTagsIndexes());
        this.eventId = eventId;
    }
}

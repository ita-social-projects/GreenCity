package greencity.dto.relevance;

import greencity.dto.cache.CachedTagsWithCoherence;
import greencity.entity.event.Event;
import lombok.Getter;

@Getter
public class EventWithTagsVectorDto extends EntityWithTagsVector {
    private final Event event;

    public EventWithTagsVectorDto(Event event, CachedTagsWithCoherence tags) {
        super(event.getTags(), tags.eventTagsIndexes());
        this.event = event;
    }
}

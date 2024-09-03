package greencity.repository;

import greencity.dto.filter.FilterEventDto;
import greencity.entity.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventSearchRepo {
    /**
     * Method for search list event ids by {@link FilterEventDto}.
     *
     * @param pageable       {@link Pageable}.
     * @param filterEventDto {@link FilterEventDto}.
     * @return list of event ids.
     */
    Page<Long> findEventsIds(Pageable pageable, FilterEventDto filterEventDto, Long userId);

    /**
     * Method for search events by title,text,short info and tag name.
     *
     * @param pageable      {@link Pageable}
     * @param searchingText - text criteria for searching.
     * @return all finding events, their tags and also count of finding events.
     * @author Anton Bondar
     */
    Page<Event> find(Pageable pageable, String searchingText);
}

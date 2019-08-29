package greencity.service;

import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.*;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.List;

/** Provides the interface to manage {@code Place} entity. */
public interface PlaceService {

    List<AdminPlaceDto> getPlacesByStatus(PlaceStatus placeStatus);

    /**
     * Update status for the Place and set the time of modification.
     *
     * @param id - place id.
     * @param status - place status.
     * @return saved PlaceStatusDto entity.
     */
    PlaceStatusDto updateStatus(Long id, PlaceStatus status);

    /**
     * Find place by it's id.
     *
     * @param id - place id.
     * @return Place entity.
     */
    Place findById(Long id);

    Place save(PlaceAddDto dto);

    Place update(Place place);

    List<Place> findAll();

    Boolean deleteById(Long id);

    /**
     * Method for getting place information
     *
     * @param id place
     * @return info about place
     * @author Dmytro Dovhal
     */
    PlaceInfoDto getAccessById(Long id);

    List<PlaceByBoundsDto> findPlacesByMapsBounds(MapBoundsDto mapBoundsDto);
}

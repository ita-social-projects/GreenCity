package greencity.service;

import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.List;

/** Provides the interface to manage {@code Place} entity. */
public interface PlaceService {

    /**
     * Finds all {@code Place} with status {@code PlaceStatus}.
     *
     * @param placeStatus a value of {@link PlaceStatus} enum.
     * @return a list of {@code Place} with the given {@code placeStatus}
     * @author Roman Zahorui
     */
    List<AdminPlaceDto> getPlacesByStatus(PlaceStatus placeStatus);

    Place updateStatus(Long placeId, PlaceStatus placeStatus);

    Place findById(Long id);

    Place save(Place place);

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

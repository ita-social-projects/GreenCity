package greencity.service;

import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlaceStatusDto;
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

    /**
     * Check place existing by id
     *
     * @param id - place id
     * @return boolean check result
     * @author Zakhar Skaletskyi
     */
    boolean existsById(Long id);
    /**
     * The method which return a list {@code PlaceByBoundsDto} with information about place, *
     * location depends on the map bounds.
     *
     * @param mapBoundsDto contains northEastLng, northEastLat,southWestLat, southWestLng of current
     *     state of map
     * @return a list of {@code PlaceByBoundsDto}
     * @author Marian Milian.
     */
    List<PlaceByBoundsDto> findPlacesByMapsBounds(MapBoundsDto mapBoundsDto);
}

package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.filter.FilterPlaceDto;
import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.*;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {@code Place} entity.
 */
public interface PlaceService {
    /**
     * Finds all {@code Place} with status {@code PlaceStatus}.
     *
     * @param placeStatus a value of {@link PlaceStatus} enum.
     * @param pageable    pageable configuration.
     * @return an object of {@link PageableDto} which contains a list of {@link AdminPlaceDto}.
     * @author Roman Zahorui
     */
    PageableDto getPlacesByStatus(PlaceStatus placeStatus, Pageable pageable);

    /**
     * Update status for the {@link Place} and set the time of modification.
     *
     * @param id     - {@link Place} id.
     * @param status - {@link Place} status.
     * @return saved {@link UpdatePlaceStatusDto} entity.
     */
    UpdatePlaceStatusDto updateStatus(Long id, PlaceStatus status);

    /**
     * Update statuses for the {@link Place}'s and set the time of modification.
     *
     * @param dto - {@link BulkUpdatePlaceStatusDto} with places id's and updated {@link PlaceStatus}
     * @return list of {@link UpdatePlaceStatusDto} with updated places and statuses.
     */
    List<UpdatePlaceStatusDto> updateStatuses(BulkUpdatePlaceStatusDto dto);

    /**
     * Find place by it's id.
     *
     * @param id - place id.
     * @return Place entity.
     */
    Place findById(Long id);

    /**
     * Method for saving proposed {@code Place} to database.
     *
     * @param dto - dto for Place entity
     * @return place {@code Place}
     * @author Kateryna Horokh
     */
    Place save(PlaceAddDto dto, String email);

    /**
     * Find all places from DB.
     *
     * @return List of places.
     */
    List<Place> findAll();

    /**
     * Delete entity from DB by id.
     *
     * @param id - Place id.
     * @return boolean.
     */
    Boolean deleteById(Long id);

    /**
     * Method for getting place information.
     *
     * @param id place
     * @return PlaceInfoDto with info about place
     * @author Dmytro Dovhal
     */
    PlaceInfoDto getInfoById(Long id);

    /**
     * Check {@link Place} existing by id.
     *
     * @param id - {@link Place} id
     * @return boolean check result
     * @author Zakhar Skaletskyi
     */
    boolean existsById(Long id);

    /**
     * The method which return a list {@code PlaceByBoundsDto} with information about place, *
     * location depends on the map bounds.
     *
     * @param filterPlaceDto contains northEastLng, northEastLat,southWestLat, southWestLng of current
     *                     state of map
     * @return a list of {@code PlaceByBoundsDto}
     * @author Marian Milian.
     */
    List<PlaceByBoundsDto> findPlacesByMapsBounds(FilterPlaceDto filterPlaceDto);

    /**
     * Get average rate of {@link Place}.
     *
     * @param id - {@link Place} id
     * @return byte rate number
     * @author Zakhar Skaletskyi
     */
    Double averageRate(Long id);

    /**
     * The method finds all {@link Place}'s filtered by the parameters contained in {@param filterDto} object.
     *
     * @param filterDto contains objects whose values determine
     *                  the filter parameters of the returned list.
     * @return a list of {@code PlaceByBoundsDto}
     * @author Roman Zahouri
     */
    List<PlaceByBoundsDto> getPlacesByFilter(FilterPlaceDto filterDto);

    /**
     * The method finds all {@link Place}'s filtered by the parameters contained in {@param filterDto} object.
     *
     * @param filterDto contains objects whose values determine
     *                 the filter parameters of the returned list.
     * @param pageable pageable configuration.
     * @return a list of {@code PlaceByBoundsDto}
     * @author Rostyslav Khasanov
     */
    PageableDto<AdminPlaceDto> filterPlaceBySearchPredicate(FilterPlaceDto filterDto, Pageable pageable);

    /**
     * Get list of available statuses of {@link Place}.
     *
     * @return available {@link Place} statuses.
     */
    List<PlaceStatus> getStatuses();
}

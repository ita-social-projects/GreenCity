package greencity.service;

import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlaceStatusDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.List;

/** Provides the interface to manage {@code Place} entity. */
public interface PlaceService {

    List<AdminPlaceDto> getPlacesByStatus(PlaceStatus placeStatus);

    PlaceStatusDto updateStatus(Long id, PlaceStatus status);

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
}

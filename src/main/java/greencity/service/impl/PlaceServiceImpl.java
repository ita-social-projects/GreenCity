package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.*;
import greencity.entity.Category;
import greencity.entity.Location;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceNotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.mapping.PlaceAddDtoMapper;
import greencity.repository.PlaceRepo;
import greencity.service.CategoryService;
import greencity.service.LocationService;
import greencity.service.OpenHoursService;
import greencity.service.PlaceService;
import greencity.util.DateTimeService;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class provides implementation of the {@code PlaceService}.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    /**
     * Autowired repository.
     */
    private PlaceRepo placeRepo;

    /**
     * Autowired mapper.
     */
    private ModelMapper modelMapper;

    private CategoryService categoryService;

    private LocationService locationService;

    private OpenHoursService openingHoursService;

    private PlaceAddDtoMapper placeAddDtoMapper;

    /**
     * Finds all {@code Place} with status {@code PlaceStatus}.
     *
     * @param placeStatus a value of {@link PlaceStatus} enum.
     * @return a list of {@code Place} with the given {@code placeStatus}
     * @author Roman Zahorui
     */
    @Override
    public List<AdminPlaceDto> getPlacesByStatus(PlaceStatus placeStatus) {
        List<Place> places = placeRepo.findAllByStatusOrderByModifiedDateDesc(placeStatus);
        return places.stream()
                .map(place -> modelMapper.map(place, AdminPlaceDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Method for saving proposed Place to database.
     *
     * @param dto - dto for Place entity
     * @return place
     * @author Kateryna Horokh
     */
    @Transactional
    @Override
    public Place save(PlaceAddDto dto) {
        log.info("in save(PlaceAddDto dto), save place - {}", dto.getName());
        Category category = createCategoryByName(dto.getCategory().getName());
        Place place = placeRepo.save(placeAddDtoMapper.convertToEntity(dto));
        place.setCategory(category);
        setPlaceToLocation(place);
        setPlaceToOpeningHours(place);

        return place;
    }

    /**
     * Method for setting OpeningHours entity with Place to database.
     *
     * @param place - Place entity
     * @author Kateryna Horokh
     */
    private void setPlaceToOpeningHours(Place place) {
        log.info("in setPlaceToOpeningHours(Place place)", place.getName());
        List<OpeningHours> hours = place.getOpeningHoursList();
        hours.forEach(
                h -> {
                    h.setPlace(place);
                    openingHoursService.save(h);
                });
    }

    /**
     * Method for setting Location entity with Place to database.
     *
     * @param place - Place entity
     * @author Kateryna Horokh
     */
    private void setPlaceToLocation(Place place) {
        log.info("in setPlaceToLocation(Place place)", place.getName());
        Location location = place.getLocation();
        location.setPlace(place);
        locationService.save(location);
    }

    /**
     * Method for creating new category to database if it does not exists by name.
     *
     * @param name - String category's name
     * @return category
     * @author Kateryna Horokh
     */
    private Category createCategoryByName(String name) {
        log.info("in setPlaceToLocation(Place place)", name);

        Category category = categoryService.findByName(name);
        if (category == null) {
            category = new Category();
            category.setName(name);
            category = categoryService.save(category);
        }
        return category;
    }

    /**
     * Method for deleting place by id.
     *
     * @param id - Long place's id
     * @return boolean
     */
    @Override
    public Boolean deleteById(Long id) {
        log.info("In deleteById() place method.");
        Place place = findById(id);
        placeRepo.delete(place);
        log.info("This place was deleted.");
        return true;
    }

    @Override
    public List<Place> findAll() {
        log.info("In findAll() place method.");
        return placeRepo.findAll();
    }

    /**
     * Update status for the Place and set the time of modification.
     *
     * @param id     - place id.
     * @param status - place status.
     * @return saved PlaceStatusDto entity.
     * @author Nazar Vladyka.
     */
    @Override
    public PlaceStatusDto updateStatus(Long id, PlaceStatus status) {
        log.info(LogMessage.IN_UPDATE_PLACE_STATUS, id, status);

        Place updatable = findById(id);

        if (updatable.getStatus().equals(status)) {
            log.error(LogMessage.PLACE_STATUS_NOT_DIFFERENT, id, status);
            throw new PlaceStatusException(
                    ErrorMessage.PLACE_STATUS_NOT_DIFFERENT + updatable.getStatus());
        } else {
            updatable.setStatus(status);
            updatable.setModifiedDate(DateTimeService.getDateTime(AppConstant.UKRAINE_TIMEZONE));
        }

        return modelMapper.map(placeRepo.save(updatable), PlaceStatusDto.class);
    }

    /**
     * Find place by it's id.
     *
     * @param id - place id.
     * @return Place entity.
     * @author Nazar Vladyka.
     */
    @Override
    public Place findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return placeRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.PLACE_NOT_FOUND_BY_ID + id));
    }

    /**
     * Save place to database.
     *
     * @param place - Place entity.
     * @return saved Place entity.
     * @author Nazar Vladyka.
     */
    @Override
    public Place save(Place place) {
        log.info("in save(Place place), save place - {}", place.getName());
        return placeRepo.saveAndFlush(place);
    }

    @Override
    public PlaceInfoDto getAccessById(Long id) {
        PlaceInfoDto placeInfoDto =
                modelMapper.map(
                        placeRepo
                                .findById(id)
                                .orElseThrow(
                                        () ->
                                                new PlaceNotFoundException(
                                                        ErrorMessage.PLACE_NOT_FOUND_BY_ID + id)),
                        PlaceInfoDto.class);
        placeInfoDto.setRate(placeRepo.averageRate(id));
        return placeInfoDto;
    }

    @Override
    public Place update(Place place) {
        return null;
    }

    /**
     * Method witch return list dto with place id , place name,place address, place latitude ,and
     * place longitude.
     *
     * @param mapBoundsDto contains northEastLng, northEastLat,southWestLat, southWestLng of current
     *                     state of map
     * @return list of dto
     * @author Marian Milian.
     */
    @Override
    public List<PlaceByBoundsDto> findPlacesByMapsBounds(@Valid MapBoundsDto mapBoundsDto) {
        log.info(
                "in findPlacesLocationByMapsBounds(MapBoundsDto mapBoundsDto), dto - {}",
                mapBoundsDto);

        List<Place> list =
                placeRepo.findPlacesByMapsBounds(
                        mapBoundsDto.getNorthEastLat(),
                        mapBoundsDto.getNorthEastLng(),
                        mapBoundsDto.getSouthWestLat(),
                        mapBoundsDto.getSouthWestLng());
        return list.stream()
                .map(place -> modelMapper.map(place, PlaceByBoundsDto.class))
                .collect(Collectors.toList());
    }

    /**
     * @author Zakhar Skaletskyi
     *
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Long id) {
        return placeRepo.existsById(id);
    }

}

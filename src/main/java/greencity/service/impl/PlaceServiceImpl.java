package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.place.PlaceAddDto;
import greencity.entity.Category;
import greencity.entity.Location;
import greencity.entity.OpeningHours;
import greencity.dto.place.AdminPlaceDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.BadIdException;
import greencity.mapping.PlaceAddDtoMapper;
import greencity.exception.NotFoundException;
import greencity.repository.PlaceRepo;
import greencity.service.*;

import greencity.service.DateTimeService;
import greencity.service.PlaceService;
import java.util.List;

import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The class provides implementation of the {@code PlaceService}. */
@Service
@AllArgsConstructor
@Slf4j
public class PlaceServiceImpl implements PlaceService {

    private PlaceRepo placeRepo;
    /** Autowired mapper. */
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
        List<Place> places = placeRepo.getPlacesByStatus(placeStatus);
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
    public Place findById(Long id) {
        log.info("In findById() method.");
        return placeRepo
                .findById(id)
                .orElseThrow(() -> new BadIdException(ErrorMessage.PLACE_NOT_FOUND_BY_ID + id));
    }

    @Override
    public List<Place> findAll() {
        log.info("In findAll() place method.");
        return placeRepo.findAll();
    }

    /**
     * Update status for the Place and set the time of modification.
     *
     * @param placeId - place id.
     * @param placeStatus - enum of Place status value.
     * @return saved Place entity.
     * @author Nazar Vladyka.
     * */
    public Place updateStatus(Long placeId, PlaceStatus placeStatus) {
        Place updatable =
                placeRepo
                        .findById(placeId)
                        .orElseThrow(
                                () -> new NotFoundException("Place not found with id " + placeId));

        updatable.setStatus(placeStatus);
        updatable.setModifiedDate(DateTimeService.getDateTime("Europe/Kiev"));

        log.info(
                "in updateStatus(Long placeId, PlaceStatus placeStatus) update place with id - {} and status - {}",
                placeId,
                placeStatus.toString());

        return placeRepo.saveAndFlush(updatable);
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
        log.info("in findById(Long id), find place with id - {}", id);

        return placeRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Place not found with id " + id));
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

    @Override
    public Place update(Place place) {
        return null;
    }
}

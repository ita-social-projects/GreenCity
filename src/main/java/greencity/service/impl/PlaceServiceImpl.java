package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.place.PlaceAddDto;
import greencity.entity.Category;
import greencity.entity.Location;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.BadIdException;
import greencity.mapping.PlaceAddDtoMapper;
import greencity.repository.PlaceRepo;
import greencity.service.*;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The class provides implementation of the {@code PlaceService}. */
@Service
@AllArgsConstructor
@Slf4j
public class PlaceServiceImpl implements PlaceService {

    private PlaceRepo placeRepo;

    private CategoryService categoryService;

    private LocationService locationService;

    private OpenHoursService openingHoursService;

    private PlaceAddDtoMapper placeAddDtoMapper;

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

    /** {@inheritDoc} */
    @Override
    public List<Place> getPlacesByStatus(PlaceStatus placeStatus) {
        return placeRepo.findPlacesByStatus(placeStatus);
    }

    @Override
    public Place update(Place place) {
        return null;
    }
}

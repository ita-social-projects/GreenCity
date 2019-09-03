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
import greencity.exception.BadLocationRequestException;
import greencity.exception.CheckRepeatingValueException;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.mapping.PlaceAddDtoMapper;
import greencity.repository.PlaceRepo;
import greencity.service.*;
import greencity.util.DateTimeService;
import io.jsonwebtoken.lang.Assert;

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
    private PlaceRepo placeRepo;

    private ModelMapper modelMapper;

    private CategoryService categoryService;

    private LocationService locationService;

    private OpenHoursService openingHoursService;

    private PlaceAddDtoMapper placeAddDtoMapper;

    private UserService userService;

    /**
     * {@inheritDoc}
     *
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
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Transactional
    @Override
    public Place save(PlaceAddDto dto, String email) {
        log.info(LogMessage.IN_SAVE);
        Category category = createCategoryByName(dto.getCategory().getName());
        Place place = placeAddDtoMapper.convertToEntity(dto);
        place.setAuthor(userService.findByEmail(email));
        place.setCategory(category);
        placeRepo.save(place);
        setPlaceToLocation(place);
        setPlaceToOpeningHours(place);

        return place;
    }

    /**
     * Method for setting {@code OpeningHours} with {@code Place} to database.
     *
     * @param place of {@link Place} entity.
     * @author Kateryna Horokh
     */
    private void setPlaceToOpeningHours(Place place) {
        log.info(LogMessage.SET_PLACE_TO_OPENING_HOURS, place.getName());

        List<OpeningHours> hours = place.getOpeningHoursList();
        checkRepeatingValue(hours);
        hours.stream()
            .distinct()
            .forEach(
                h -> {
                    h.setPlace(place);
                    openingHoursService.save(h);
                });
    }

    /**
     * Method for checking list of giving {@code OpeningHours} on repeating value of week days.
     *
     * @param hours - list of {@link OpeningHours} entity.
     * @author Kateryna Horokh
     */
    private void checkRepeatingValue(List<OpeningHours> hours) {
        log.info(LogMessage.CHECK_REPEATING_VALUE);
        for (int i = 0; i < hours.size(); i++) {
            for (int j = i + 1; j < hours.size(); j++) {
                if (hours.get(i).getWeekDay() == (hours.get(j).getWeekDay())) {
                    throw new CheckRepeatingValueException(ErrorMessage.REPEATING_VALUE_OF_WEEKDAY_VALUE);
                }
            }
        }
    }

    /**
     * Method for setting {@code Location} with {@code Place} to database.
     *
     * @param place of {@link Place} entity.
     * @author Kateryna Horokh
     */
    private void setPlaceToLocation(Place place) {
        log.info(LogMessage.SET_PLACE_TO_LOCATION, place.getName());

        Location location =
            locationService.findByLatAndLng(
                place.getLocation().getLat(), place.getLocation().getLng());
        if (location == null) {
            location = place.getLocation();
            location.setPlace(place);
            locationService.save(location);
        } else {
            throw new BadLocationRequestException(
                ErrorMessage.LOCATION_ALREADY_EXISTS_BY_THIS_COORDINATES);
        }
    }

    /**
     * Method for creating new {@code Category} to database if it does not exists by name.
     *
     * @param name - String category's name
     * @return category of {@link Category} entity.
     * @author Kateryna Horokh
     */
    private Category createCategoryByName(String name) {
        log.info(LogMessage.CREATE_CATEGORY_BY_NAME, name);

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
     * @author Kateryn Horokh
     */
    @Override
    public Boolean deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID);
        Place place = findById(id);
        placeRepo.delete(place);
        return true;
    }

    @Override
    public List<Place> findAll() {
        log.info(LogMessage.IN_FIND_ALL);
        return placeRepo.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka.
     */
    @Override
    public PlaceStatusDto updateStatus(Long id, PlaceStatus status) {
        log.info(LogMessage.IN_UPDATE_PLACE_STATUS, id, status);

        Place updatable = findById(id);
        Assert.notNull(updatable.getStatus(), ErrorMessage.PLACE_STATUS_IS_NULL);

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
     * {@inheritDoc}
     *
     * @author Nazar Vladyka.
     */
    @Override
    public Place findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return placeRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.PLACE_NOT_FOUND_BY_ID + id));
    }

    @Override
    public PlaceInfoDto getAccessById(Long id) {
        PlaceInfoDto placeInfoDto =
            modelMapper.map(
                placeRepo
                    .findById(id)
                    .orElseThrow(
                        () ->
                            new NotFoundException(
                                ErrorMessage.PLACE_NOT_FOUND_BY_ID + id)),
                PlaceInfoDto.class);
        placeInfoDto.setRate(placeRepo.averageRate(id));
        return placeInfoDto;
    }

    /**
     * {@inheritDoc}
     *
     * @author Marian Milian
     */
    @Override
    public List<PlaceByBoundsDto> findPlacesByMapsBounds(@Valid MapBoundsDto mapBoundsDto) {
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
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public boolean existsById(Long id) {
        return placeRepo.existsById(id);
    }
}

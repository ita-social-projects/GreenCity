package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.*;
import greencity.entity.*;
import greencity.entity.enums.PlaceStatus;
import greencity.entity.enums.ROLE;
import greencity.exception.CheckRepeatingValueException;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.repository.PlaceRepo;
import greencity.service.*;
import greencity.util.DateTimeService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class provides implementation of the {@code PlaceService}.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    private static final PlaceStatus APPROVED_STATUS = PlaceStatus.APPROVED;
    private PlaceRepo placeRepo;
    private ModelMapper modelMapper;
    private CategoryService categoryService;
    private LocationService locationService;
    private OpenHoursService openingHoursService;
    private UserService userService;
    private SpecificationService specificationService;
    private DiscountService discountService;

    /**
     * {@inheritDoc}
     *
     * @author Roman Zahorui
     */
    @Override
    public PlacePageableDto getPlacesByStatus(PlaceStatus placeStatus, Pageable pageable) {
        Page<Place> places = placeRepo.findAllByStatusOrderByModifiedDateDesc(placeStatus, pageable);
        List<AdminPlaceDto> list = places.stream()
            .map(place -> modelMapper.map(place, AdminPlaceDto.class))
            .collect(Collectors.toList());
        return new PlacePageableDto(list, places.getTotalElements(), places.getPageable().getPageNumber());
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
        Category category = categoryService.findByName(dto.getCategory().getName());

        Place place = modelMapper.map(dto, Place.class);
        place.setAuthor(userService.findByEmail(email));
        if (place.getAuthor().getRole() == ROLE.ROLE_ADMIN || place.getAuthor().getRole() == ROLE.ROLE_MODERATOR) {
            place.setStatus(PlaceStatus.APPROVED);
        }
        place.setCategory(category);
        placeRepo.save(place);
        setPlaceToLocation(place);
        setPlaceToOpeningHours(place);
        setToDiscountPlaceAndCategoty(category, place);

        return place;
    }

    /**
     * Method for setting {@code Discount} with {@code Place} and {@code Category} to database.
     *
     * @param place    of {@link Place} entity.
     * @param category of {@link Category} entity.
     * @author Kateryna Horokh
     */
    private void setToDiscountPlaceAndCategoty(Category category, Place place) {
        List<Discount> discounts = place.getDiscounts();
        discounts.stream().forEach(val -> {
            Specification specification = specificationService.findByName(val.getSpecification().getName());
            val.setSpecification(specification);
            val.setPlace(place);
            val.setCategory(category);
            discountService.save(val);
        });
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
     * Method for setting {@code Location} with {@code Place} to database.
     *
     * @param place of {@link Place} entity.
     * @author Kateryna Horokh
     */
    private void setPlaceToLocation(Place place) {
        log.info(LogMessage.SET_PLACE_TO_LOCATION, place.getName());

        Location location = place.getLocation();
        location.setPlace(place);
        locationService.save(location);
    }

    /**
     * Method for checking list of giving {@code OpeningHours} on repeating value of week days.
     *
     * @param hours - list of {@link OpeningHours} entity.
     * @author Kateryna Horokh
     */
    private void checkRepeatingValue(List<OpeningHours> hours) {
        for (int i = 0; i < hours.size(); i++) {
            for (int j = i + 1; j < hours.size(); j++) {
                if (hours.get(i).getWeekDay().equals(hours.get(j).getWeekDay())) {
                    throw new CheckRepeatingValueException(ErrorMessage.REPEATING_VALUE_OF_WEEKDAY_VALUE);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Transactional
    @Override
    public Place update(Long id, PlaceUpdateDto dto) {
        log.info(LogMessage.IN_UPDATE, dto);

        Place updatedPlace = findById(id);

        Category updatedCategory = categoryService.findByName(dto.getCategory().getName());

        updatedPlace.setName(dto.getName());
        updatedPlace.setCategory(updatedCategory);
        placeRepo.save(updatedPlace);
        setPlaceToLocation(updatedPlace);
        setPlaceToOpeningHours(updatedPlace);
        setToDiscountPlaceAndCategoty(updatedCategory, updatedPlace);

        return updatedPlace;
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
        if (!updatable.getStatus().equals(status)) {
            updatable.setStatus(status);
            updatable.setModifiedDate(DateTimeService.getDateTime(AppConstant.UKRAINE_TIMEZONE));
        } else {
            log.error(LogMessage.PLACE_STATUS_NOT_DIFFERENT, id, status);
            throw new PlaceStatusException(
                ErrorMessage.PLACE_STATUS_NOT_DIFFERENT + updatable.getStatus());
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

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public PlaceInfoDto getInfoById(Long id) {
        Place place =
            placeRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.PLACE_NOT_FOUND_BY_ID + id));
        PlaceInfoDto placeInfoDto = modelMapper.map(place, PlaceInfoDto.class);
        placeInfoDto.setRate(placeRepo.getAverageRate(id));
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
                mapBoundsDto.getSouthWestLng(),
                APPROVED_STATUS);
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
        log.info(LogMessage.IN_EXISTS_BY_ID, id);
        return placeRepo.existsById(id);
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public Double averageRate(Long id) {
        log.info(LogMessage.IN_AVERAGE_RATE, id);
        return placeRepo.getAverageRate(id);
    }
}

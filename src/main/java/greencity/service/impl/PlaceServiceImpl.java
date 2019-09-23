package greencity.service.impl;

import static greencity.constant.AppConstant.CONSTANT_OF_FORMULA_HAVERSINE_KM;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableDto;
import greencity.dto.discount.DiscountDtoForUpdatePlace;
import greencity.dto.filter.FilterDistanceDto;
import greencity.dto.filter.FilterPlaceDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursUpdateDto;
import greencity.dto.place.*;
import greencity.entity.*;
import greencity.entity.enums.PlaceStatus;
import greencity.entity.enums.ROLE;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.repository.PlaceRepo;
import greencity.repository.options.PlaceFilter;
import greencity.service.*;
import greencity.util.DateTimeService;
import java.util.*;
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
    private EmailService emailService;

    /**
     * {@inheritDoc}
     *
     * @author Roman Zahorui
     */
    @Override

    public PageableDto getPlacesByStatus(PlaceStatus placeStatus, Pageable pageable) {
        Page<Place> places = placeRepo.findAllByStatusOrderByModifiedDateDesc(placeStatus, pageable);
        List<AdminPlaceDto> list = places.stream()
            .map(place -> modelMapper.map(place, AdminPlaceDto.class))
            .collect(Collectors.toList());
        return new PageableDto(list, places.getTotalElements(), places.getPageable().getPageNumber());
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Transactional
    @Override
    public Place save(PlaceAddDto dto, String email) {
        log.info(LogMessage.IN_SAVE, dto.getName(), email);

        Category category = categoryService.findByName(dto.getCategory().getName());
        Place place = modelMapper.map(dto, Place.class);

        place.setAuthor(userService.findByEmail(email).orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL)));

        if (place.getAuthor().getRole() == ROLE.ROLE_ADMIN || place.getAuthor().getRole() == ROLE.ROLE_MODERATOR) {
            place.setStatus(APPROVED_STATUS);
        }

        place.setCategory(category);
        Location locationWithPlace = saveLocationWithPlace(dto.getLocation(), place);
        place.setLocation(locationWithPlace);
        placeRepo.save(place);
        setPlaceToOpeningHours(place);
        setToDiscountPlaceAndCategoty(category, place);
        return place;
    }

    private Location saveLocationWithPlace(LocationAddressAndGeoDto dto, Place place) {
        Location location = modelMapper.map(dto, Location.class);
        location.setPlace(place);
        return location;
    }

    private void setToDiscountPlaceAndCategoty(Category category, Place place) {
        log.info(LogMessage.SET_PLACE_TO_DISCOUNTS, place.getName(), category.getName());

        Set<Discount> discounts = place.getDiscounts();
        discounts.forEach(val -> {
            Specification specification = specificationService.findByName(val.getSpecification().getName());
            val.setSpecification(specification);
            val.setPlace(place);
            val.setCategory(category);
            discountService.save(val);
        });
    }

    private void setPlaceToOpeningHours(Place place) {
        log.info(LogMessage.SET_PLACE_TO_OPENING_HOURS, place.getName());

        Set<OpeningHours> hours = place.getOpeningHoursList();
        hours.stream()
            .distinct()
            .forEach(
                h -> {
                    h.setPlace(place);
                    openingHoursService.save(h);
                });
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Transactional
    @Override
    public Place update(Long id, PlaceUpdateDto dto) {
        log.info(LogMessage.IN_UPDATE, dto.getName());

        Category updatedCategory = categoryService.findByName(dto.getCategory().getName());
        Place updatedPlace = findById(id);
        updatedPlace.setName(dto.getName());
        updatedPlace.setCategory(updatedCategory);
        updateLocationOfUpdatedPlace(dto, updatedPlace);
        placeRepo.save(updatedPlace);

        updateOpeningHoursForUpdatedPlace(dto, updatedPlace);
        updateDiscountForUpdatedPlace(dto, updatedCategory, updatedPlace);

        return updatedPlace;
    }

    private void updateDiscountForUpdatedPlace(PlaceUpdateDto dto, Category updatedCategory, Place updatedPlace) {
        log.info(LogMessage.IN_UPDATE_DISCOUNT_FOR_PLACE, dto.getName());

        Set<DiscountDtoForUpdatePlace> discountList = dto.getDiscounts();
        Set<Discount> discountsOld = discountService.findAllByPlaceId(updatedPlace.getId());
        discountService.deleteAllByPlaceId(updatedPlace.getId());
        Set<Discount> discounts = new HashSet<>();
        discountList.forEach(d -> {
            Discount discount;
            discount = modelMapper.map(d, Discount.class);
            Specification specification = specificationService.findByName(d.getSpecification().getName());
            discount.setPlace(updatedPlace);
            discount.setCategory(updatedCategory);
            discount.setSpecification(specification);
            discountService.save(discount);
            discounts.add(discount);
        });
        discountsOld.addAll(discounts);
    }

    private void updateOpeningHoursForUpdatedPlace(PlaceUpdateDto dto, Place updatedPlace) {
        log.info(LogMessage.IN_UPDATE_OPENING_HOURS_FOR_PLACE, dto.getName());

        Set<OpeningHoursUpdateDto> hoursUpdateDtoSet = dto.getOpeningHoursList();
        Set<OpeningHours> openingHoursSetOld = openingHoursService.findAllByPlaceId(updatedPlace.getId());
        openingHoursService.deleteAllByPlaceId(updatedPlace.getId());
        Set<OpeningHours> hours = new HashSet<>();
        hoursUpdateDtoSet.forEach(h -> {
            OpeningHours openingHours;
            openingHours = modelMapper.map(h, OpeningHours.class);
            openingHours.setPlace(updatedPlace);
            openingHoursService.save(openingHours);
            hours.add(openingHours);
        });
        openingHoursSetOld.addAll(hours);
    }

    private void updateLocationOfUpdatedPlace(PlaceUpdateDto dto, Place updatedPlace) {
        Location location = locationService.findByPlaceId(updatedPlace.getId());
        modelMapper.map(dto.getLocation(), location);
    }

    /**
     * Method for deleting place by id.
     *
     * @param id - Long place's id
     * @return boolean
     * @author Kateryna Horokh
     */
    @Override
    public Boolean deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID);
        Place place = findById(id);
        placeRepo.delete(place);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka.
     */
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
    public UpdatePlaceStatusDto updateStatus(Long id, PlaceStatus status) {
        log.info(LogMessage.IN_UPDATE_PLACE_STATUS, id, status);

        Place updatable = findById(id);
        if (!updatable.getStatus().equals(status)) {
            //if status was proposed and it's changes, means approve/declines by Admin
            if (updatable.getStatus().equals(PlaceStatus.PROPOSED)) {
                emailService.sendChangePlaceStatusNotification(updatable, status);
            }
            updatable.setStatus(status);
            updatable.setModifiedDate(DateTimeService.getDateTime(AppConstant.UKRAINE_TIMEZONE));
        } else {
            log.error(LogMessage.PLACE_STATUS_NOT_DIFFERENT, id, status);
            throw new PlaceStatusException(
                updatable.getId() + ErrorMessage.PLACE_STATUS_NOT_DIFFERENT + updatable.getStatus());
        }

        return modelMapper.map(placeRepo.save(updatable), UpdatePlaceStatusDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Transactional
    @Override
    public List<UpdatePlaceStatusDto> updateStatuses(BulkUpdatePlaceStatusDto dto) {
        List<UpdatePlaceStatusDto> updatedPlaces = new ArrayList<>();

        for (Long id : dto.getIds()) {
            updatedPlaces.add(updateStatus(id, dto.getStatus()));
        }

        return updatedPlaces;
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
     * @author Kateryna Horokh
     */
    @Override
    public PlaceUpdateDto getInfoForUpdatingById(Long id) {
        Place place = placeRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.PLACE_NOT_FOUND_BY_ID + id));
        PlaceUpdateDto placeUpdateDto = modelMapper.map(place, PlaceUpdateDto.class);
        return placeUpdateDto;
    }

    /**
     * {@inheritDoc}
     *
     * @author Marian Milian
     */
    @Override
    public List<PlaceByBoundsDto> findPlacesByMapsBounds(@Valid FilterPlaceDto filterPlaceDto) {
        List<Place> list = placeRepo.findAll(new PlaceFilter(filterPlaceDto));
        return list.stream()
            .map(place -> modelMapper.map(place, PlaceByBoundsDto.class))
            .collect(Collectors.toList());
    }

    private List<Long> getPlaceBoundsId(List<PlaceByBoundsDto> listB) {
        List<Long> result = new ArrayList<Long>();
        listB.forEach(el -> result.add(el.getId()));
        return result;
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

    /**
     * {@inheritDoc}
     *
     * @author Roman Zahorui
     */
    @Override
    public List<PlaceByBoundsDto> getPlacesByFilter(FilterPlaceDto filterDto) {
        List<Place> list = placeRepo.findAll(new PlaceFilter(filterDto));
        list = getPlacesByDistanceFromUser(filterDto, list);
        return list.stream()
            .map(place -> modelMapper.map(place, PlaceByBoundsDto.class))
            .collect(Collectors.toList());
    }

    /**
     * Method that filtering places by distance.
     *
     * @param filterDto - {@link FilterPlaceDto} DTO.
     * @param placeList - {@link List} of {@link Place} that will be filtered.
     * @return {@link List} of {@link Place} - list of filtered {@link Place}s.
     * @author Nazar Stasyuk
     */
    private List<Place> getPlacesByDistanceFromUser(FilterPlaceDto filterDto, List<Place> placeList) {
        FilterDistanceDto distanceFromUserDto = filterDto.getDistanceFromUserDto();
        if (distanceFromUserDto != null
            && distanceFromUserDto.getLat() != null
            && distanceFromUserDto.getLng() != null
            && distanceFromUserDto.getDistance() != null) {
            placeList = placeList.stream().filter(place -> {
                double userLatRad = Math.toRadians(distanceFromUserDto.getLat());
                double userLngRad = Math.toRadians(distanceFromUserDto.getLng());
                double placeLatRad = Math.toRadians(place.getLocation().getLat());
                double placeLngRad = Math.toRadians(place.getLocation().getLng());

                double distance = CONSTANT_OF_FORMULA_HAVERSINE_KM * Math.acos(
                    Math.cos(userLatRad)
                        * Math.cos(placeLatRad)
                        * Math.cos(placeLngRad - userLngRad)
                        + Math.sin(userLatRad)
                        * Math.sin(placeLatRad));
                return distance <= distanceFromUserDto.getDistance();
            }).collect(Collectors.toList());
        }
        return placeList;
    }

    /**
     * {@inheritDoc}
     *
     * @author Rostyslav Khasanov
     */
    @Override
    public PageableDto<AdminPlaceDto> filterPlaceBySearchPredicate(FilterPlaceDto filterDto, Pageable pageable) {
        Page<Place> list = placeRepo.findAll(new PlaceFilter(filterDto), pageable);
        List<AdminPlaceDto> adminPlaceDtos =
            list.getContent().stream()
                .map(user -> modelMapper.map(user, AdminPlaceDto.class))
                .collect(Collectors.toList());
        return new PageableDto<AdminPlaceDto>(
            adminPlaceDtos,
            list.getTotalElements(),
            list.getPageable().getPageNumber());
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public List<PlaceStatus> getStatuses() {
        return Arrays.asList(PlaceStatus.class.getEnumConstants());
    }
}

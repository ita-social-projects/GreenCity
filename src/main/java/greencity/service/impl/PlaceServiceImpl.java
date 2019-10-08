package greencity.service.impl;

import static greencity.constant.AppConstant.CONSTANT_OF_FORMULA_HAVERSINE_KM;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.filter.FilterDistanceDto;
import greencity.dto.filter.FilterPlaceDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.place.*;
import greencity.entity.*;
import greencity.entity.enums.PlaceStatus;
import greencity.entity.enums.ROLE;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.mapping.DiscountValueMapper;
import greencity.mapping.ProposePlaceMapper;
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
    private ProposePlaceMapper placeMapper;
    private CategoryService categoryService;
    private LocationService locationService;
    private DiscountValueMapper discountValueMapper;
    private UserService userService;
    private EmailService emailService;
    private OpenHoursService openingHoursService;
    private DiscountService discountService;

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

        Place place = placeMapper.convertToEntity(dto);
        setUserToPlaceByEmail(email, place);
        return placeRepo.save(place);
    }

    /**
     * Method for getting {@link User} and set this {@link User} to place.
     *
     * @param email - String, user's email.
     * @param place - {@link Place} entity.
     * @return user - {@link User}.
     * @author Kateryna Horokh
     */
    private User setUserToPlaceByEmail(String email, Place place) {
        User user = userService.findByEmail(email).orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        place.setAuthor(user);

        if (user.getRole() == ROLE.ROLE_ADMIN || user.getRole() == ROLE.ROLE_MODERATOR) {
            place.setStatus(APPROVED_STATUS);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Transactional
    @Override
    public Place update(PlaceUpdateDto dto) {
        log.info(LogMessage.IN_UPDATE, dto.getName());

        Category updatedCategory = categoryService.findByName(dto.getCategory().getName());
        Place updatedPlace = findById(dto.getId());
        locationService.update(updatedPlace.getLocation().getId(), modelMapper.map(dto.getLocation(), Location.class));
        updatedPlace.setName(dto.getName());
        updatedPlace.setCategory(updatedCategory);
        placeRepo.save(updatedPlace);

        updateOpening(dto.getOpeningHoursList(), updatedPlace);
        updateDiscount(dto.getDiscounts(), updatedPlace);

        return updatedPlace;
    }

    /**
     * Method for updating set of {@link DiscountValue} and save with new {@link Category} and {@link Place}.
     *
     * @param discounts    - set of {@link DiscountValue}.
     * @param updatedPlace - {@link Place} entity.
     * @author Kateryna Horokh
     */
    private void updateDiscount(Set<DiscountValueDto> discounts, Place updatedPlace) {
        log.info(LogMessage.IN_UPDATE_DISCOUNT_FOR_PLACE);

        Set<DiscountValue> discountsOld = discountService.findAllByPlaceId(updatedPlace.getId());
        discountService.deleteAllByPlaceId(updatedPlace.getId());
        Set<DiscountValue> newDiscounts = new HashSet<>();
        discounts.forEach(d -> {
            DiscountValue discount = discountValueMapper.convertToEntity(d);
            discount.setPlace(updatedPlace);
            discountService.save(discount);
            newDiscounts.add(discount);
        });
        discountsOld.addAll(newDiscounts);
    }

    /**
     * Method for updating set of {@link OpeningHours} and save with new {@link Place}.
     *
     * @param hoursUpdateDtoSet - set of {@code Discount}.
     * @param updatedPlace      - {@link Place} entity.
     * @author Kateryna Horokh
     */
    private void updateOpening(Set<OpeningHoursDto> hoursUpdateDtoSet, Place updatedPlace) {
        log.info(LogMessage.IN_UPDATE_OPENING_HOURS_FOR_PLACE);

        Set<OpeningHours> openingHoursSetOld = openingHoursService.findAllByPlaceId(updatedPlace.getId());
        openingHoursService.deleteAllByPlaceId(updatedPlace.getId());
        Set<OpeningHours> hours = new HashSet<>();
        hoursUpdateDtoSet.forEach(h -> {
            OpeningHours openingHours = modelMapper.map(h, OpeningHours.class);
            openingHours.setPlace(updatedPlace);
            openingHoursService.save(openingHours);
            hours.add(openingHours);
        });
        openingHoursSetOld.addAll(hours);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public Long deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        updateStatus(id, PlaceStatus.DELETED);

        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Transactional
    @Override
    public Long bulkDelete(List<Long> ids) {
        log.info(LogMessage.IN_BULK_DELETE, ids);

        List<UpdatePlaceStatusDto> deletedPlaces =
            updateStatuses(new BulkUpdatePlaceStatusDto(ids, PlaceStatus.DELETED));

        return (long) deletedPlaces.size();
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
     * @author Nazar Vladyka
     */
    @Override
    public UpdatePlaceStatusDto updateStatus(Long id, PlaceStatus status) {
        log.info(LogMessage.IN_UPDATE_PLACE_STATUS, id, status);

        Place updatable = findById(id);
        PlaceStatus oldStatus = updatable.getStatus();

        checkPlaceStatuses(oldStatus, status, id);

        updatable.setStatus(status);
        updatable.setModifiedDate(DateTimeService.getDateTime(AppConstant.UKRAINE_TIMEZONE));

        // if place had status PROPOSED and it changes, means APPROVEs or DECLINEs we send an email
        if (oldStatus.equals(PlaceStatus.PROPOSED)) {
            emailService.sendChangePlaceStatusEmail(updatable);
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
        log.info(LogMessage.IN_UPDATE_PLACE_STATUSES, dto);

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

    private void checkPlaceStatuses(PlaceStatus currentStatus, PlaceStatus updatedStatus, Long placeId) {
        if (currentStatus.equals(updatedStatus)) {
            log.error(LogMessage.PLACE_STATUS_NOT_DIFFERENT, placeId, updatedStatus);
            throw new PlaceStatusException(String.format(
                ErrorMessage.PLACE_STATUS_NOT_DIFFERENT, placeId, updatedStatus));
        }
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
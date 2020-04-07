package greencity.service.impl;

import static greencity.constant.AppConstant.CONSTANT_OF_FORMULA_HAVERSINE_KM;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import static greencity.constant.RabbitConstants.CHANGE_PLACE_STATUS_ROUTING_KEY;
import greencity.dto.PageableDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.filter.FilterDistanceDto;
import greencity.dto.filter.FilterPlaceDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.place.*;
import greencity.entity.*;
import greencity.entity.enums.PlaceStatus;
import greencity.entity.enums.ROLE;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.PlaceStatusException;
import greencity.mapping.DiscountValueMapper;
import greencity.mapping.ProposePlaceMapper;
import greencity.message.SendChangePlaceStatusEmailMessage;
import greencity.repository.PlaceRepo;
import greencity.repository.options.PlaceFilter;
import greencity.service.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class provides implementation of the {@code PlaceService}.
 */
@Slf4j
@Service
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepo placeRepo;
    private final ModelMapper modelMapper;
    private final ProposePlaceMapper placeMapper;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final SpecificationService specificationService;
    private final UserService userService;
    private final OpenHoursService openingHoursService;
    private final DiscountService discountService;
    private final NotificationService notificationService;
    private final ZoneId datasourceTimezone;
    private final RabbitTemplate rabbitTemplate;
    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;

    /**
     * Constructor.
     */
    @Autowired
    public PlaceServiceImpl(PlaceRepo placeRepo,
                            ModelMapper modelMapper,
                            ProposePlaceMapper placeMapper,
                            CategoryService categoryService,
                            LocationService locationService,
                            SpecificationService specificationService,
                            UserService userService,
                            OpenHoursService openingHoursService,
                            DiscountService discountService,
                            NotificationService notificationService,
                            @Qualifier(value = "datasourceTimezone") ZoneId datasourceTimezone,
                            RabbitTemplate rabbitTemplate) {
        this.placeRepo = placeRepo;
        this.modelMapper = modelMapper;
        this.placeMapper = placeMapper;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.specificationService = specificationService;
        this.userService = userService;
        this.openingHoursService = openingHoursService;
        this.discountService = discountService;
        this.notificationService = notificationService;
        this.datasourceTimezone = datasourceTimezone;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * {@inheritDoc}
     *
     * @author Roman Zahorui
     */
    @Override
    public PageableDto<AdminPlaceDto> getPlacesByStatus(PlaceStatus placeStatus, Pageable pageable) {
        Page<Place> places = placeRepo.findAllByStatusOrderByModifiedDateDesc(placeStatus, pageable);
        List<AdminPlaceDto> list = places.stream()
            .map(place -> modelMapper.map(place, AdminPlaceDto.class))
            .collect(Collectors.toList());
        return new PageableDto<>(list, places.getTotalElements(), places.getPageable().getPageNumber());
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
        place.getPhotos().forEach(photo -> photo.setUser(place.getAuthor()));
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
        User user = userService.findByEmail(email);
        place.setAuthor(user);
        if (user.getRole() == ROLE.ROLE_ADMIN || user.getRole() == ROLE.ROLE_MODERATOR) {
            place.setStatus(PlaceStatus.APPROVED);
            notificationService.sendImmediatelyReport(place);
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
        updateDiscount(dto.getDiscountValues(), updatedPlace);

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
            DiscountValue discount = modelMapper.map(d, DiscountValue.class);
            discount.setSpecification(specificationService.findByName(d.getSpecification().getName()));
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
    public void deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        updateStatus(id, PlaceStatus.DELETED);
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
        updatable.setModifiedDate(ZonedDateTime.now(datasourceTimezone));
        if (status.equals(PlaceStatus.APPROVED)) {
            notificationService.sendImmediatelyReport(updatable);
        }
        if (oldStatus.equals(PlaceStatus.PROPOSED)) {
            rabbitTemplate.convertAndSend(sendEmailTopic, CHANGE_PLACE_STATUS_ROUTING_KEY,
                new SendChangePlaceStatusEmailMessage(updatable.getAuthor().getFirstName(),
                    updatable.getName(), updatable.getStatus().toString().toLowerCase(),
                    updatable.getAuthor().getEmail()));
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
     * @author Marian Milian
     */
    @Override
    public Optional<Place> findByIdOptional(Long id) {
        return placeRepo.findById(id);
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
        return modelMapper.map(place, PlaceUpdateDto.class);
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
        return new PageableDto<>(
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

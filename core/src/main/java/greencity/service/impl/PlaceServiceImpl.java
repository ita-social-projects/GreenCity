package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.filter.FilterDistanceDto;
import greencity.dto.filter.FilterPlaceDto;
import greencity.dto.location.LocationVO;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.openinghours.OpeningHoursVO;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.BulkUpdatePlaceStatusDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlaceUpdateDto;
import greencity.dto.place.PlaceVO;
import greencity.dto.place.UpdatePlaceStatusDto;
import greencity.entity.Category;
import greencity.entity.DiscountValue;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.entity.Specification;
import greencity.entity.User;
import greencity.enums.PlaceStatus;
import greencity.enums.ROLE;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.PlaceStatusException;
import greencity.message.SendChangePlaceStatusEmailMessage;
import greencity.repository.PlaceRepo;
import greencity.repository.options.PlaceFilter;
import greencity.service.CategoryService;
import greencity.service.DiscountService;
import greencity.service.LocationService;
import greencity.service.NotificationService;
import greencity.service.OpenHoursService;
import greencity.service.PlaceService;
import greencity.service.ProposePlaceService;
import greencity.service.SpecificationService;
import greencity.service.UserService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static greencity.constant.AppConstant.CONSTANT_OF_FORMULA_HAVERSINE_KM;
import static greencity.constant.RabbitConstants.CHANGE_PLACE_STATUS_ROUTING_KEY;

/**
 * The class provides implementation of the {@code PlaceService}.
 */
@Slf4j
@Service
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepo placeRepo;
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final SpecificationService specificationService;
    private final UserService userService;
    private final OpenHoursService openingHoursService;
    private final DiscountService discountService;
    private final NotificationService notificationService;
    private final ZoneId datasourceTimezone;
    private final RabbitTemplate rabbitTemplate;
    private final ProposePlaceService proposePlaceService;
    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;

    /**
     * Constructor.
     */
    @Autowired
    public PlaceServiceImpl(PlaceRepo placeRepo,
                            ModelMapper modelMapper,
                            CategoryService categoryService,
                            LocationService locationService,
                            SpecificationService specificationService,
                            UserService userService,
                            OpenHoursService openingHoursService,
                            DiscountService discountService,
                            NotificationService notificationService,
                            @Qualifier(value = "datasourceTimezone") ZoneId datasourceTimezone,
                            RabbitTemplate rabbitTemplate,
                            ProposePlaceServiceImpl proposePlaceService) {
        this.placeRepo = placeRepo;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.specificationService = specificationService;
        this.userService = userService;
        this.openingHoursService = openingHoursService;
        this.discountService = discountService;
        this.notificationService = notificationService;
        this.datasourceTimezone = datasourceTimezone;
        this.rabbitTemplate = rabbitTemplate;
        this.proposePlaceService = proposePlaceService;
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
        return new PageableDto<>(list, places.getTotalElements(), places.getPageable().getPageNumber(),
            places.getTotalPages());
    }

    /**
     * {@inheritDoc}
     *
     * @author Marian Datsko
     */
    @Transactional
    @Override
    public Place save(PlaceAddDto dto, String email) {
        log.info(LogMessage.IN_SAVE, dto.getName(), email);

        proposePlaceService.checkLocationValues(dto.getLocation());
        if (dto.getOpeningHoursList() != null) {
            proposePlaceService.checkInputTime(dto.getOpeningHoursList());
        }

        Place place = modelMapper.map(dto, Place.class);
        setUserToPlaceByEmail(email, place);

        place.setCategory(modelMapper.map(categoryService.findByName(dto.getCategory().getName()), Category.class));
        proposePlaceService.saveDiscountValuesWithPlace(place.getDiscountValues(), place);
        proposePlaceService.savePhotosWithPlace(place.getPhotos(), place);

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
            notificationService.sendImmediatelyReport(modelMapper.map(place, PlaceVO.class));
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

        Category updatedCategory = modelMapper.map(
            categoryService.findByName(dto.getCategory().getName()), Category.class);
        Place updatedPlace = findById(dto.getId());
        locationService.update(updatedPlace.getLocation().getId(),
            modelMapper.map(dto.getLocation(), LocationVO.class));
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
        if (discounts != null) {
            discounts.forEach(d -> {
                DiscountValue discount = modelMapper.map(d, DiscountValue.class);
                discount.setSpecification(modelMapper
                    .map(specificationService.findByName(d.getSpecification().getName()), Specification.class));
                discount.setPlace(updatedPlace);
                discountService.save(discount);
                newDiscounts.add(discount);
            });
        }
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
        Set<OpeningHoursVO> openingHoursVO = openingHoursService.findAllByPlaceId(updatedPlace.getId());
        Set<OpeningHours> openingHoursSetOld = modelMapper.map(openingHoursVO,
            new TypeToken<Set<OpeningHours>>() {
            }.getType());
        openingHoursService.deleteAllByPlaceId(updatedPlace.getId());
        Set<OpeningHours> hours = new HashSet<>();
        if (hoursUpdateDtoSet != null) {
            hoursUpdateDtoSet.forEach(h -> {
                OpeningHours openingHours = modelMapper.map(h, OpeningHours.class);
                openingHours.setPlace(updatedPlace);
                openingHoursService.save(modelMapper.map(openingHours, OpeningHoursVO.class));
                hours.add(openingHours);
            });
        }
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
     * @author Olena Petryshak.
     */
    @Override
    public PageableDto<AdminPlaceDto> findAll(Pageable pageable) {
        log.info(LogMessage.IN_FIND_ALL);

        Page<Place> pages = placeRepo.findAll(pageable);
        List<AdminPlaceDto> placeDtos =
            pages.stream().map(place -> modelMapper.map(place, AdminPlaceDto.class)).collect(Collectors.toList());

        return new PageableDto<>(placeDtos, pages.getTotalElements(), pageable.getPageNumber(), pages.getTotalPages());
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
            notificationService.sendImmediatelyReport(modelMapper.map(updatable, PlaceVO.class));
        }
        if (oldStatus.equals(PlaceStatus.PROPOSED)) {
            rabbitTemplate.convertAndSend(sendEmailTopic, CHANGE_PLACE_STATUS_ROUTING_KEY,
                new SendChangePlaceStatusEmailMessage(updatable.getAuthor().getName(),
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
     * @author Olena Petryshak
     */
    @Override
    public PageableDto<AdminPlaceDto> searchBy(Pageable pageable, String searchQuery) {
        Page<Place> pages = placeRepo.searchBy(pageable, searchQuery);
        List<AdminPlaceDto> adminPlaceDtos = pages.stream()
            .map(place -> modelMapper.map(place, AdminPlaceDto.class))
            .collect(Collectors.toList());
        return new PageableDto<>(
            adminPlaceDtos,
            pages.getTotalElements(),
            pageable.getPageNumber(),
            pages.getTotalPages()
        );
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
            list.getPageable().getPageNumber(),
            list.getTotalPages());
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

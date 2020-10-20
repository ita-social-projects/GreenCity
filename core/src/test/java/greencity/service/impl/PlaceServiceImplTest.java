package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.category.CategoryDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.discount.DiscountValueVO;
import greencity.dto.filter.FilterDistanceDto;
import greencity.dto.filter.FilterPlaceDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.location.LocationVO;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.openinghours.OpeningHoursVO;
import greencity.dto.photo.PhotoAddDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.BulkUpdatePlaceStatusDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlaceUpdateDto;
import greencity.dto.place.UpdatePlaceStatusDto;
import greencity.entity.Category;
import greencity.entity.DiscountValue;
import greencity.entity.Location;
import greencity.entity.OpeningHours;
import greencity.entity.Photo;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.enums.PlaceStatus;
import greencity.enums.ROLE;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.PlaceStatusException;
import greencity.repository.PlaceRepo;
import greencity.repository.options.PlaceFilter;
import greencity.service.CategoryService;
import greencity.service.DiscountService;
import greencity.service.LocationServiceImpl;
import greencity.service.NotificationService;
import greencity.service.OpenHoursService;
import greencity.service.PlaceService;
import greencity.service.SpecificationService;
import greencity.service.UserService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
class PlaceServiceImplTest {
    private final Category category = Category.builder()
        .id(1L)
        .name("test").build();

    private final CategoryDto categoryDto = CategoryDto.builder()
        .name("test")
        .build();

    private final User user =
        User.builder()
            .id(1L)
            .email("Nazar.stasyuk@gmail.com")
            .name("Nazar Stasyuk")
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    private final Place genericEntity1 = Place.builder()
        .id(1L)
        .name("test1")
        .author(user)
        .status(PlaceStatus.PROPOSED)
        .modifiedDate(ZonedDateTime.now())
        .build();
    private final Place genericEntity2 = Place.builder()
        .id(2L)
        .name("test2")
        .author(user)
        .status(PlaceStatus.PROPOSED)
        .modifiedDate(ZonedDateTime.now())
        .build();
    private final LocationAddressAndGeoDto locationDto = LocationAddressAndGeoDto.builder()
        .address("test")
        .lat(45.456)
        .lng(46.456)
        .build();
    private final Location location = Location.builder()
        .id(1L)
        .address("test")
        .lat(45.456)
        .lng(46.456)
        .build();
    private final LocationVO locationVO = LocationVO.builder()
        .id(1L)
        .address("test")
        .lat(45.456)
        .lng(46.456)
        .build();
    private Set<OpeningHoursDto> openingHoursList = new HashSet<>();
    private Set<OpeningHours> openingHoursListEntity = new HashSet<>();
    private Set<OpeningHoursVO> openingHoursListEntityVO = new HashSet<>();
    private Set<DiscountValue> discountValues = new HashSet<>();
    private Set<DiscountValueDto> discountValuesDto = new HashSet<>();
    private Set<DiscountValueVO> discountValuesVO = new HashSet<>();
    private List<PhotoAddDto> photoDtos = new ArrayList<>();
    private List<Photo> photos = new ArrayList<>();
    private Place place = Place.builder()
        .id(1L)
        .name("Test")
        .category(category)
        .author(user)
        .location(location)
        .openingHoursList(openingHoursListEntity)
        .discountValues(discountValues)
        .photos(photos)
        .status(PlaceStatus.PROPOSED)
        .build();
    private PlaceAddDto placeAddDto = PlaceAddDto.
        builder()
        .name("Test")
        .category(categoryDto)
        .location(locationDto)
        .openingHoursList(openingHoursList)
        .discountValues(discountValuesDto)
        .photos(photoDtos)
        .build();
    @Mock
    private PlaceRepo placeRepo;
    @Mock
    private CategoryService categoryService;
    @Mock
    private LocationServiceImpl locationService;
    @Mock
    private OpenHoursService openingHoursService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserService userService;
    @Mock
    private ProposePlaceServiceImpl proposePlaceMapper;
    @Mock
    private SpecificationService specificationService;
    @Mock
    private DiscountService discountService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private RabbitTemplate rabbitTemplate;
    private ZoneId zoneId = ZoneId.of("Europe/Kiev");
    private PlaceService placeService;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        placeService = new PlaceServiceImpl(placeRepo, modelMapper, categoryService,
            locationService, specificationService, userService, openingHoursService, discountService,
            notificationService, zoneId, rabbitTemplate, proposePlaceMapper);
    }

    @Test
    void saveTest() {
        when(modelMapper.map(placeAddDto, Place.class)).thenReturn(place);
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(modelMapper.map(categoryService.findByName(anyString()), Category.class)).thenReturn(category);
        when(placeRepo.save(place)).thenReturn(place);

        assertEquals(place, placeService.save(placeAddDto, user.getEmail()));
    }

    @Test
    void updateStatusTest() {
        User user = User.builder().name("test fname").email("test.ua").build();
        Place genericEntity = Place.builder()
            .id(1L)
            .name("test")
            .author(user)
            .status(PlaceStatus.PROPOSED)
            .modifiedDate(ZonedDateTime.now())
            .build();
        doNothing().when(rabbitTemplate).convertAndSend((Object) any(), any(), any());
        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.save(any())).thenReturn(genericEntity);
        placeService.updateStatus(1L, PlaceStatus.DECLINED);
        assertEquals(PlaceStatus.DECLINED, genericEntity.getStatus());
    }

    @Test
    void getPlacesByStatusTest() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Place place = new Place();
        place.setName("Place");

        AdminPlaceDto dto = new AdminPlaceDto();
        dto.setName("Place");

        Page<Place> placesPage = new PageImpl<>(Collections.singletonList(place), pageable, 1);
        List<AdminPlaceDto> listDto = Collections.singletonList(dto);

        PageableDto<AdminPlaceDto> pageableDto = new PageableDto<>(listDto, listDto.size(), 0, 1);
        pageableDto.setPage(listDto);

        when(placeRepo.findAllByStatusOrderByModifiedDateDesc(any(), any())).thenReturn(placesPage);
        when(modelMapper.map(any(), any())).thenReturn(dto);

        assertEquals(pageableDto, placeService.getPlacesByStatus(any(), any()));
        verify(placeRepo, times(1)).findAllByStatusOrderByModifiedDateDesc(any(), any());
    }

    @Test
    void updateStatusGivenTheSameStatusThenThrowException() {
        Place genericEntity = Place.builder().id(1L).status(PlaceStatus.PROPOSED).build();

        when(placeRepo.findById(1L)).thenReturn(Optional.of(genericEntity));

        assertThrows(PlaceStatusException.class, () -> placeService.updateStatus(1L, PlaceStatus.PROPOSED));
    }

    @Test
    void updateStatusGivenPlaceIdNullThenThrowException() {
        assertThrows(NotFoundException.class, () -> placeService.updateStatus(null, PlaceStatus.PROPOSED));
    }

    @Test
    void findByIdTest() {
        Place genericEntity = new Place();
        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        Place foundEntity = placeService.findById(anyLong());
        assertEquals(genericEntity, foundEntity);
    }

    @Test
    void findByIdGivenIdNullThenThrowException() {
        assertThrows(NotFoundException.class, () -> placeService.findById(null));
    }

    @Test
    void findPlaceUpdateDtoTest() {
        Place genericEntity = new Place();
        PlaceUpdateDto placeUpdateDto = new PlaceUpdateDto();
        when(placeRepo.findById(1L)).thenReturn(Optional.of(genericEntity));
        when(modelMapper.map(genericEntity, PlaceUpdateDto.class)).thenReturn(placeUpdateDto);
        PlaceUpdateDto foundEntity = placeService.getInfoForUpdatingById(1L);
        assertEquals(placeUpdateDto, foundEntity);

        verify(placeRepo).findById(1L);
        verify(modelMapper).map(genericEntity, PlaceUpdateDto.class);
    }

    @Test
    void getInfoByIdTest() {
        PlaceInfoDto gen = new PlaceInfoDto();
        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(place));
        when(modelMapper.map(any(), any())).thenReturn(gen);
        when(placeRepo.getAverageRate(anyLong())).thenReturn(1.5);
        PlaceInfoDto res = placeService.getInfoById(anyLong());
        assertEquals(gen, res);
    }

    @Test
    void getInfoByIdNotFoundTest() {
        assertThrows(NotFoundException.class, () -> placeService.getInfoById(null));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    void existsById() {
        when(placeRepo.existsById(anyLong())).thenReturn(true);
        assertTrue(placeService.existsById(3L));
        when(placeRepo.existsById(anyLong())).thenReturn(false);
        assertFalse(placeService.existsById(2L));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    void averageRate() {
        Double averageRate = 4.0;
        when(placeRepo.getAverageRate(anyLong())).thenReturn(averageRate);
        assertEquals(averageRate, placeService.averageRate(2L));
    }

    @Test
    void updateStatusesTest() {
        BulkUpdatePlaceStatusDto requestDto = new BulkUpdatePlaceStatusDto(
            Arrays.asList(1L, 2L),
            PlaceStatus.DECLINED
        );

        List<UpdatePlaceStatusDto> expected = Arrays.asList(
            new UpdatePlaceStatusDto(1L, PlaceStatus.DECLINED),
            new UpdatePlaceStatusDto(2L, PlaceStatus.DECLINED)
        );

        when(placeRepo.findById(anyLong()))
            .thenReturn(Optional.of(genericEntity1))
            .thenReturn(Optional.of(genericEntity2));
        when(modelMapper.map(any(), any()))
            .thenReturn(new UpdatePlaceStatusDto(1L, PlaceStatus.DECLINED))
            .thenReturn(new UpdatePlaceStatusDto(2L, PlaceStatus.DECLINED));

        assertEquals(expected, placeService.updateStatuses(requestDto));
    }

    @Test
    void getStatusesTest() {
        List<PlaceStatus> placeStatuses =
            Arrays.asList(PlaceStatus.PROPOSED, PlaceStatus.DECLINED, PlaceStatus.APPROVED, PlaceStatus.DELETED);

        assertEquals(placeStatuses, placeService.getStatuses());
    }

    @Test
    void bulkDelete() {
        List<Long> request = Arrays.asList(1L, 2L);

        when(placeRepo.findById(anyLong()))
            .thenReturn(Optional.of(genericEntity1))
            .thenReturn(Optional.of(genericEntity2));
        when(modelMapper.map(any(), any()))
            .thenReturn(new UpdatePlaceStatusDto(1L, PlaceStatus.DELETED))
            .thenReturn(new UpdatePlaceStatusDto(2L, PlaceStatus.DELETED));

        assertEquals(2L, placeService.bulkDelete(request));
    }

    @Test
    void findAllTest() {
        List<Place> expectedList = Arrays.asList(new Place(), new Place());

        when(placeRepo.findAll()).thenReturn(expectedList);
        assertEquals(expectedList, placeService.findAll());
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Place> pages = new PageImpl<>(Collections.singletonList(place), pageable, 1);
        when(placeRepo.findAll(pageable)).thenReturn(pages);
        List<AdminPlaceDto> placeDtos =
            pages.stream().map(place -> modelMapper.map(place, AdminPlaceDto.class)).collect(Collectors.toList());
        PageableDto<AdminPlaceDto> result =
            new PageableDto<>(placeDtos, pages.getTotalElements(), pageable.getPageNumber(), pages.getTotalPages());
        assertEquals(result, placeService.findAll(pageable));
        verify(placeRepo).findAll(pageable);
    }

    @Test
    void updateTest() {
        PlaceUpdateDto placeUpdateDto = new PlaceUpdateDto();
        placeUpdateDto.setId(1L);
        placeUpdateDto.setOpeningHoursList(openingHoursList);
        placeUpdateDto.setDiscountValues(discountValuesDto);
        placeUpdateDto.setName("new Name");
        placeUpdateDto.setCategory(categoryDto);
        when(modelMapper.map(categoryService.findByName(category.getName()), Category.class)).thenReturn(category);
        when(placeRepo.findById(placeUpdateDto.getId())).thenReturn(Optional.of(place));
        when(modelMapper.map(placeUpdateDto.getLocation(), LocationVO.class)).thenReturn(locationVO);
        when(modelMapper.map(openingHoursListEntityVO, new TypeToken<Set<OpeningHours>>() {
        }.getType())).thenReturn(openingHoursListEntity);
        when(modelMapper.map(discountValuesVO, new TypeToken<Set<DiscountValue>>() {
        }.getType())).thenReturn(discountValues);

        Place updatedPlace = placeService.update(placeUpdateDto);

        assertEquals(placeUpdateDto.getName(), updatedPlace.getName());
        assertEquals(placeUpdateDto.getCategory().getName(), updatedPlace.getCategory().getName());
        verify(modelMapper).map(placeUpdateDto.getLocation(), LocationVO.class);
        verify(locationService).update(place.getLocation().getId(), locationVO);
    }

    @Test
    void deleteByIdTest() {
        when(placeRepo.findById(place.getId())).thenReturn(Optional.of(place));
        when(placeRepo.save(place)).thenReturn(place);

        placeService.deleteById(place.getId());

        assertEquals(PlaceStatus.DELETED, place.getStatus());
        verify(notificationService, never()).sendImmediatelyReport(any());
    }

    @Test
    void findByIdOptionalTest() {
        when(placeRepo.findById(place.getId())).thenReturn(Optional.of(place));

        Optional<Place> resultOptional = placeService.findByIdOptional(place.getId());

        assertTrue(resultOptional.isPresent());
        assertSame(place, resultOptional.get());
    }

    @Test
    void getInfoForUpdatingByIdTest() {
        PlaceUpdateDto placeUpdateDto = new PlaceUpdateDto();
        placeUpdateDto.setId(place.getId());
        when(placeRepo.findById(place.getId())).thenReturn(Optional.of(place));
        when(modelMapper.map(place, PlaceUpdateDto.class)).thenReturn(placeUpdateDto);

        placeUpdateDto = placeService.getInfoForUpdatingById(place.getId());

        assertEquals(place.getId(), placeUpdateDto.getId());
        verify(placeRepo).findById(place.getId());
        verify(modelMapper).map(place, PlaceUpdateDto.class);
    }

    @Test
    void getInfoForUpdatingThrowingExceptionTest() {
        when(placeRepo.findById(place.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> placeService.getInfoForUpdatingById(1L));
        verify(placeRepo).findById(place.getId());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void findPlacesByMapsBoundsTest() {
        List<Place> places = Collections.singletonList(place);
        PlaceByBoundsDto placeByBoundsDto = new PlaceByBoundsDto();
        FilterPlaceDto filterPlaceDto = new FilterPlaceDto();
        List<PlaceByBoundsDto> dtoList = Collections.singletonList(placeByBoundsDto);
        when(placeRepo.findAll(any(PlaceFilter.class))).thenReturn(places);
        when(modelMapper.map(place, PlaceByBoundsDto.class)).thenReturn(placeByBoundsDto);

        List<PlaceByBoundsDto> result = placeService.findPlacesByMapsBounds(filterPlaceDto);

        assertEquals(dtoList, result);
        verify(placeRepo).findAll(any(PlaceFilter.class));
        verify(modelMapper).map(place, PlaceByBoundsDto.class);
    }

    @Test
    void getPlacesByFilterWithNullDistanceFromUserTest() {
        List<Place> places = Collections.singletonList(place);
        FilterPlaceDto filterDto = new FilterPlaceDto();
        PlaceByBoundsDto placeByBoundsDto = new PlaceByBoundsDto();
        placeByBoundsDto.setId(place.getId());
        List<PlaceByBoundsDto> placeByBoundsDtos = Collections.singletonList(placeByBoundsDto);
        when(placeRepo.findAll((any(PlaceFilter.class)))).thenReturn(places);
        when(modelMapper.map(place, PlaceByBoundsDto.class)).thenReturn(placeByBoundsDto);

        List<PlaceByBoundsDto> result = placeService.getPlacesByFilter(filterDto);

        assertEquals(placeByBoundsDtos, result);
        verify(placeRepo).findAll(any(PlaceFilter.class));
        verify(modelMapper).map(place, PlaceByBoundsDto.class);
    }

    @Test
    void getPlacesByFilterWithDistanceFromUserTest() {
        Location newLocation = new Location();
        newLocation.setLat(-80.0);
        newLocation.setLng(-170.0);
        genericEntity1.setLocation(newLocation);
        List<Place> places = Arrays.asList(place, genericEntity1);
        FilterPlaceDto filterDto = new FilterPlaceDto();
        filterDto.setDistanceFromUserDto(new FilterDistanceDto(-80.0, -170.0, 500.0));
        PlaceByBoundsDto placeByBoundsDto = new PlaceByBoundsDto();
        List<PlaceByBoundsDto> placeByBoundsDtos = Collections.singletonList(placeByBoundsDto);
        when(placeRepo.findAll((any(PlaceFilter.class)))).thenReturn(places);
        when(modelMapper.map(genericEntity1, PlaceByBoundsDto.class)).thenReturn(placeByBoundsDto);

        List<PlaceByBoundsDto> result = placeService.getPlacesByFilter(filterDto);

        assertEquals(placeByBoundsDtos, result);
        verify(placeRepo).findAll(any(PlaceFilter.class));
        verify(modelMapper).map(genericEntity1, PlaceByBoundsDto.class);
    }

    @Test
    void filterPlaceBySearchPredicateTest() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Place> pageOfPlaces = new PageImpl<>(Collections.singletonList(place), pageable, 1);
        when(placeRepo.findAll(any(PlaceFilter.class), any(Pageable.class))).thenReturn(pageOfPlaces);
        AdminPlaceDto adminPlaceDto = new AdminPlaceDto();
        PageableDto<AdminPlaceDto> adminPlacePage =
            new PageableDto<>(Collections.singletonList(adminPlaceDto), 1, 0, 1);
        when(modelMapper.map(place, AdminPlaceDto.class)).thenReturn(adminPlaceDto);

        PageableDto<AdminPlaceDto> result = placeService.filterPlaceBySearchPredicate(new FilterPlaceDto(), pageable);

        assertEquals(adminPlacePage, result);
        verify(placeRepo).findAll(any(PlaceFilter.class), any(Pageable.class));
        verify(modelMapper).map(place, AdminPlaceDto.class);
    }

    @Test
    void searchByTest() {
        String searchQuery = "test";
        Pageable pageable = PageRequest.of(0, 1);
        Page<Place> pages = new PageImpl<>(Collections.singletonList(place), pageable, 1);
        when(placeRepo.searchBy(pageable, searchQuery)).thenReturn(pages);
        List<AdminPlaceDto> placeDtos =
            pages.stream().map(place -> modelMapper.map(place, AdminPlaceDto.class)).collect(Collectors.toList());
        PageableDto<AdminPlaceDto> result =
            new PageableDto<>(placeDtos, pages.getTotalElements(), pageable.getPageNumber(), pages.getTotalPages());
        assertEquals(result, placeService.searchBy(pageable, searchQuery));
        verify(placeRepo).searchBy(pageable, searchQuery);
    }
}

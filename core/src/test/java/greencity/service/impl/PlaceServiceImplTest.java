package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.category.CategoryDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.filter.FilterDistanceDto;
import greencity.dto.filter.FilterPlaceDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.photo.PhotoAddDto;
import greencity.dto.place.*;
import greencity.entity.*;
import greencity.entity.enums.PlaceStatus;
import greencity.entity.enums.ROLE;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.PlaceStatusException;
import greencity.repository.PlaceRepo;
import greencity.repository.options.PlaceFilter;
import greencity.service.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
class PlaceServiceImplTest {
    Category category = Category.builder()
        .id(1L)
        .name("test").build();

    CategoryDto categoryDto = CategoryDto.builder()
        .name("test")
        .build();

    User user =
        User.builder()
            .id(1L)
            .email("Nazar.stasyuk@gmail.com")
            .name("Nazar Stasyuk")
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();

    LocationAddressAndGeoDto locationDto = LocationAddressAndGeoDto.builder()
        .address("test")
        .lat(45.456)
        .lng(46.456)
        .build();

    Location location = Location.builder()
        .id(1L)
        .address("test")
        .lat(45.456)
        .lng(46.456)
        .build();

    Set<OpeningHoursDto> openingHoursList = new HashSet<>();

    Set<OpeningHours> openingHoursListEntity = new HashSet<>();

    Set<DiscountValue> discountValues = new HashSet<>();

    Set<DiscountValueDto> discountValuesDto = new HashSet<>();

    List<PhotoAddDto> photoDtos = new ArrayList<>();

    List<Photo> photos = new ArrayList<>();

    Place place = Place.builder()
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


    PlaceAddDto placeAddDto = PlaceAddDto.
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
        when(categoryService.findByName(anyString())).thenReturn(category);
        when(placeRepo.save(place)).thenReturn(place);

        assertEquals(place, placeService.save(placeAddDto, user.getEmail()));
    }

    @Test
    public void updateStatusTest() {
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
        assertThrows(PlaceStatusException.class, () -> {
            Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();
            when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
            placeService.updateStatus(1L, PlaceStatus.PROPOSED);
        });
    }

    @Test
    void updateStatusGivenPlaceIdNullThenThrowException() {
        assertThrows(NotFoundException.class, () -> {
            placeService.updateStatus(null, PlaceStatus.PROPOSED);
        });
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
        assertThrows(NotFoundException.class, () -> {
            placeService.findById(null);
        });
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
        assertThrows(NotFoundException.class, () -> {
            placeService.getInfoById(null);
        });
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

        assertEquals(new Long(2), placeService.bulkDelete(request));
    }

    @Test
    void findAllTest() {
        List<Place> expectedList = Arrays.asList(new Place(), new Place());

        when(placeRepo.findAll()).thenReturn(expectedList);

        assertEquals(expectedList, placeService.findAll());
    }

    @Test
    void updateTest() {
        PlaceUpdateDto placeUpdateDto = new PlaceUpdateDto();
        placeUpdateDto.setOpeningHoursList(openingHoursList);
        placeUpdateDto.setDiscountValues(discountValuesDto);
        placeUpdateDto.setName("new Name");
        placeUpdateDto.setCategory(categoryDto);
        when(categoryService.findByName(any())).thenReturn(category);
        when(placeRepo.findById(any())).thenReturn(Optional.of(place));

        Place updatedPlace = placeService.update(placeUpdateDto);

        assertEquals(placeUpdateDto.getName(), updatedPlace.getName());
        assertEquals(placeUpdateDto.getCategory().getName(), updatedPlace.getCategory().getName());
        verify(locationService).update(any(), any());
    }

    @Test
    void deleteByIdTest() {
        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(place));
        when(placeRepo.save(any())).thenReturn(place);

        placeService.deleteById(place.getId());

        assertEquals(PlaceStatus.DELETED, place.getStatus());
        verify(notificationService, times(0)).sendImmediatelyReport(any());
    }

    @Test
    void findByIdOptionalTest() {
        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(place));

        Optional<Place> resultOptional = placeService.findByIdOptional(place.getId());

        assertTrue(resultOptional.isPresent());
        assertSame(place, resultOptional.get());
    }

    @Test
    void getInfoForUpdatingByIdTest() {
        PlaceUpdateDto placeUpdateDto = new PlaceUpdateDto();
        placeUpdateDto.setId(place.getId());
        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(place));
        when(modelMapper.map(any(), any())).thenReturn(placeUpdateDto);

        placeUpdateDto = placeService.getInfoForUpdatingById(place.getId());

        assertEquals(place.getId(), placeUpdateDto.getId());
        verify(placeRepo).findById(place.getId());
        verify(modelMapper).map(place, PlaceUpdateDto.class);
    }

    @Test
    void getInfoForUpdatingThrowingExceptionTest() {
        when(placeRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> placeService.getInfoForUpdatingById(1L));
        verify(placeRepo).findById(anyLong());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void findPlacesByMapsBoundsTest() {
        List<Place> places = Collections.singletonList(place);
        PlaceByBoundsDto placeByBoundsDto = new PlaceByBoundsDto();
        FilterPlaceDto filterPlaceDto = new FilterPlaceDto();
        List<PlaceByBoundsDto> dtoList = Collections.singletonList(placeByBoundsDto);
        when(placeRepo.findAll(any(PlaceFilter.class))).thenReturn(places);
        when(modelMapper.map(any(), any())).thenReturn(placeByBoundsDto);

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
        when(modelMapper.map(any(), any())).thenReturn(placeByBoundsDto);

        List<PlaceByBoundsDto> result = placeService.getPlacesByFilter(filterDto);

        assertEquals(placeByBoundsDtos, result);
        verify(placeRepo).findAll(new PlaceFilter(any()));
        verify(modelMapper).map(any(), any());
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
        verify(modelMapper).map(any(), any());
    }

    @Test
    void filterPlaceBySearchPredicateTest() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Place> pageOfPlaces = new PageImpl<>(Collections.singletonList(place), pageable, 1);
        when(placeRepo.findAll(any(PlaceFilter.class), any(Pageable.class))).thenReturn(pageOfPlaces);
        AdminPlaceDto adminPlaceDto = new AdminPlaceDto();
        PageableDto<AdminPlaceDto> adminPlacePage =
            new PageableDto<>(Collections.singletonList(adminPlaceDto), 1, 0, 1);
        when(modelMapper.map(any(), any())).thenReturn(adminPlaceDto);

        PageableDto<AdminPlaceDto> result = placeService.filterPlaceBySearchPredicate(new FilterPlaceDto(), pageable);

        assertEquals(adminPlacePage, result);
        verify(placeRepo).findAll(any(PlaceFilter.class), any(Pageable.class));
        verify(modelMapper).map(any(), any());
    }
}
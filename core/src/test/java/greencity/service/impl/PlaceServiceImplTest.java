package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.category.CategoryDto;
import greencity.dto.discount.DiscountValueDto;
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
        placeService = new PlaceServiceImpl(placeRepo, modelMapper , categoryService,
            locationService, specificationService, userService, openingHoursService, discountService,
            notificationService, zoneId, rabbitTemplate,proposePlaceMapper);
    }

    @Test
    void saveTest() {
        when(modelMapper.map(placeAddDto,Place.class)).thenReturn(place);
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

        PageableDto<AdminPlaceDto> pageableDto = new PageableDto<>(listDto, listDto.size(), 0);
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
}
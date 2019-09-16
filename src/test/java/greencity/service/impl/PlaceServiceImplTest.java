package greencity.service.impl;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import greencity.dto.PageableDto;
import greencity.dto.category.CategoryDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.location.MapBoundsDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.place.*;
import greencity.entity.*;
import greencity.entity.enums.PlaceStatus;
import greencity.entity.enums.ROLE;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.repository.CategoryRepo;
import greencity.repository.PlaceRepo;
import greencity.service.CategoryService;
import greencity.service.LocationService;
import greencity.service.OpenHoursService;
import greencity.service.UserService;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class PlaceServiceImplTest {
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
            .firstName("Nazar")
            .lastName("Stasyuk")
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

    List<OpeningHoursDto> openingHoursList = new ArrayList<>();

    List<OpeningHours> openingHoursListEntity = new ArrayList<>();

    OpeningHours openingHoursEntity = OpeningHours.builder()
        .id(1L)
        .openTime(LocalTime.parse("10:30"))
        .closeTime(LocalTime.parse("20:30"))
        .weekDay(DayOfWeek.MONDAY)
        .build();

    Place place = Place.builder()
        .id(1L)
        .name("Test")
        .category(category)
        .author(user)
        .location(location)
        .openingHoursList(openingHoursListEntity)
        .status(PlaceStatus.APPROVED)
        .build();

    PlaceAddDto dto = PlaceAddDto.
        builder()
        .name(place.getName())
        .category(categoryDto)
        .location(locationDto)
        .openingHoursList(openingHoursList)
        .build();


    @Mock
    private PlaceRepo placeRepo;

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private CategoryService categoryService;

    @Mock
    private LocationServiceImpl locationService;

    @Mock
    private LocationService service;

    @Mock
    private OpenHoursService openingHoursService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private PlaceServiceImpl placeService;

    @Test
    public void savePlaceWithVerificationAllParametersTest() throws Exception {
        when(modelMapper.map(any(), any())).thenReturn(place);
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        place.setAuthor(user);
        place.setCategory(category);
        openingHoursEntity.setPlace(place);
        location.setPlace(place);
        when(placeRepo.save(place)).thenReturn(place);
        assertEquals(place, placeService.save(dto, user.getEmail()));
    }

    @Test
    public void deleteByIdTest() {
        Place placeToDelete = new Place();
        Mockito.when(placeRepo.findById(1L)).thenReturn(Optional.of(placeToDelete));
        Assert.assertEquals(true, placeService.deleteById(1L));
    }

    @Test
    public void updateStatusTest() {
        Place genericEntity = Place.builder().id(1L).status(PlaceStatus.PROPOSED).build();
        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.save(any())).thenReturn(genericEntity);
        placeService.updateStatus(1L, PlaceStatus.DECLINED);
        assertEquals(PlaceStatus.DECLINED, genericEntity.getStatus());
    }

    @Test
    public void getPlacesByStatusTest() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Place place = new Place();
        place.setName("Place");

        AdminPlaceDto dto = new AdminPlaceDto();
        dto.setName("Place");

        Page<Place> placesPage = new PageImpl<>(Collections.singletonList(place), pageable, 1);
        List<AdminPlaceDto> listDto = Collections.singletonList(dto);

        PageableDto pageableDto =
            new PageableDto(listDto, listDto.size(), 0);
        pageableDto.setPage(listDto);

        when(placeRepo.findAllByStatusOrderByModifiedDateDesc(any(), any())).thenReturn(placesPage);
        when(modelMapper.map(any(), any())).thenReturn(dto);

        Assert.assertEquals(pageableDto, placeService.getPlacesByStatus(any(), any()));
        verify(placeRepo, times(1)).findAllByStatusOrderByModifiedDateDesc(any(), any());
    }

    @Test(expected = PlaceStatusException.class)
    public void updateStatusGivenTheSameStatusThenThrowException() {
        Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();
        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        placeService.updateStatus(1L, PlaceStatus.PROPOSED);
    }

    @Test(expected = NotFoundException.class)
    public void updateStatusGivenPlaceIdNullThenThrowException() {
        Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();
        placeService.updateStatus(null, PlaceStatus.PROPOSED);
    }

    @Test
    public void findByIdTest() {
        Place genericEntity = new Place();
        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        Place foundEntity = placeService.findById(anyLong());
        assertEquals(genericEntity, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdGivenIdNullThenThrowException() {
        placeService.findById(null);
    }

    @Test
    public void findPlacesByMapsBoundsTest() {
        MapBoundsDto mapBoundsDto = new MapBoundsDto(20.0, 60.0, 60.0, 10.0);
        List<Place> placeExpected =
            new ArrayList<Place>() {
                {
                    add(Place.builder().name("MyPlace").id(1L).build());
                }
            };
        when(placeRepo.findPlacesByMapsBounds(
            mapBoundsDto.getNorthEastLat(),
            mapBoundsDto.getNorthEastLng(),
            mapBoundsDto.getSouthWestLat(),
            mapBoundsDto.getSouthWestLng(),
            PlaceStatus.APPROVED))
            .thenReturn(placeExpected);
        assertEquals(
            placeExpected.size(), placeService.findPlacesByMapsBounds(mapBoundsDto).size());
    }

    @Test
    public void getInfoByIdTest() {
        PlaceInfoDto gen = new PlaceInfoDto();
        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(place));
        when(modelMapper.map(any(), any())).thenReturn(gen);
        when(placeRepo.getAverageRate(anyLong())).thenReturn(1.5);
        PlaceInfoDto res = placeService.getInfoById(anyLong());
        assertEquals(gen, res);
    }

    @Test(expected = NotFoundException.class)
    public void getInfoByIdNotFoundTest() {
        placeService.getInfoById(null);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void existsById() {
        when(placeRepo.existsById(anyLong())).thenReturn(true);
        assertTrue(placeService.existsById(3L));
        when(placeRepo.existsById(anyLong())).thenReturn(false);
        assertFalse(placeService.existsById(2L));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void averageRate() {
        Double averageRate = 4.0;
        when(placeRepo.getAverageRate(anyLong())).thenReturn(averageRate);
        assertEquals(averageRate, placeService.averageRate(2L));
    }

    @Test
    public void deleteTest() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        when(placeRepo.findById(anyLong()))
            .thenReturn(Optional.of(new Place()))
            .thenReturn(Optional.of(new Place()))
            .thenReturn(Optional.of(new Place()));

        assertEquals(new PlaceBulkDeleteDto(3L), placeService.delete(ids));
    }

    @Test
    public void getStatusesTest() {
        PlaceStatus[] placeStatuses = {PlaceStatus.PROPOSED, PlaceStatus.DECLINED, PlaceStatus.APPROVED, PlaceStatus.DELETED};

        assertEquals(new StatusDto(placeStatuses), placeService.getStatuses());
    }
}

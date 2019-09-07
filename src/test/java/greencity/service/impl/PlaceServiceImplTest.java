package greencity.service.impl;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import greencity.dto.category.CategoryDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.location.MapBoundsDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlacePageableDto;
import greencity.dto.userownsecurity.UserRegisterDto;
import greencity.entity.*;
import greencity.entity.enums.PlaceStatus;
import greencity.entity.enums.ROLE;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.repository.PlaceRepo;
import greencity.service.CategoryService;
import greencity.service.LocationService;
import greencity.service.OpenHoursService;
import greencity.service.UserService;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
            .email("Nazar.stasyuk@gmail.com")
            .firstName("Nazar")
            .lastName("Stasyuk")
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    UserRegisterDto dto =
        UserRegisterDto.builder()
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .password("123123123")
            .build();

    OpeningHoursDto openingHoursDto1 = OpeningHoursDto.builder()
        .openTime(LocalTime.parse("10:30"))
        .closeTime(LocalTime.parse("20:30"))
        .weekDay(DayOfWeek.TUESDAY)
        .build();
    OpeningHoursDto openingHoursDto2 = OpeningHoursDto.builder()
        .openTime(LocalTime.parse("10:30"))
        .closeTime(LocalTime.parse("20:30"))
        .weekDay(DayOfWeek.MONDAY)
        .build();

    LocationAddressAndGeoDto locationDto = LocationAddressAndGeoDto.builder()
        .address("test")
        .lat(45.456)
        .lng(46.456)
        .build();

    Place placeEntity = Place.builder()
        .id(1L)
        .name("Test")
        .category(category)
        .author(user)
        .build();

    List<OpeningHoursDto> openingHoursList = Arrays.asList(openingHoursDto1, openingHoursDto2);

    PlaceAddDto placeAddDto = PlaceAddDto.
        builder()
        .name(placeEntity.getName())
        .category(categoryDto)
        .location(locationDto)
        .openingHoursList(openingHoursList)
        .build();

    Location location = Location.builder()
        .id(1L)
        .address("test address")
        .lat(45.456)
        .lng(46.456)
        .place(placeEntity)
        .build();

    OpeningHours openingHours = OpeningHours.builder()
        .id(1L)
        .openTime(LocalTime.parse("10:30"))
        .closeTime(LocalTime.parse("20:30"))
        .weekDay(DayOfWeek.MONDAY)
        .place(placeEntity)
        .build();

    String email;

    @Mock
    private PlaceRepo placeRepo;

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

    @Mock
    private Place place;

    @InjectMocks
    private PlaceServiceImpl placeService;

    @Test
    public void savePlaceWithVerificationAllParametersTest() {
        when(categoryService.findByName(anyString())).thenReturn(any());
        when(categoryService.save(categoryDto)).thenReturn(category);
        when(placeRepo.save(any())).thenReturn(place);
        when(userService.findByEmail(anyString())).thenReturn(null);
        when(service.findByLatAndLng(anyDouble(), anyDouble())).thenReturn(location);
        when(openingHoursService.save(openingHours)).thenReturn(openingHours);
        assertEquals(place, placeRepo.save(place));
        assertEquals(location, service.findByLatAndLng(45.456, 45.456));
        assertEquals(openingHours, openingHoursService.save(openingHours));
        assertEquals(category, categoryService.save(categoryDto));
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

        PlacePageableDto pageableDto =
            new PlacePageableDto(listDto, listDto.size(), 0);
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
            mapBoundsDto.getSouthWestLng()))
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

}

package greencity.service.impl;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.dto.category.CategoryDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.location.MapBoundsDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.userownsecurity.UserRegisterDto;
import greencity.entity.*;
import greencity.entity.enums.PlaceStatus;
import greencity.entity.enums.ROLE;
import greencity.exception.BadLocationRequestException;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.mapping.PlaceAddDtoMapper;
import greencity.repository.PlaceRepo;
import greencity.service.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class PlaceServiceImplTest {
    Category category = Category.builder()
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

    Place place = Place.builder()
        .id(1L)
        .name("Test")
        .category(category)
        .author(user)
        .build();

    List<OpeningHoursDto> openingHoursDtoList = Arrays.asList(openingHoursDto1, openingHoursDto2);

    PlaceAddDto placeAddDto = PlaceAddDto.
        builder()
        .name(place.getName())
        .category(categoryDto)
        .location(locationDto)
        .openingHoursList(openingHoursDtoList)
        .build();

    Location location = Location.builder()
        .id(1L)
        .address("test address")
        .lat(45.456)
        .lng(46.456)
        .place(place)
        .build();

    OpeningHours openingHours = OpeningHours.builder()
        .id(1L)
        .openTime(LocalTime.parse("10:30"))
        .closeTime(LocalTime.parse("20:30"))
        .weekDay(DayOfWeek.MONDAY)
        .place(place)
        .build();

    String email;

    @Mock
    private PlaceRepo placeRepo;

    @Mock
    private CategoryService categoryService;

    @Mock
    private LocationServiceImpl locationService;

    @Mock
    private OpenHoursService openingHoursService;

    @Mock
    private PlaceAddDtoMapper placeAddDtoMapper;


    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private PlaceServiceImpl placeService;

    @Before
    public void init(){
        placeService = new PlaceServiceImpl(placeRepo,modelMapper, categoryService,
            locationService, openingHoursService, placeAddDtoMapper, userService);
    }

    @Test
    public void save() {
        when(placeAddDtoMapper.convertToEntity(placeAddDto)).thenReturn(place);
        when(placeRepo.save(place)).thenReturn(place);
    }

    @Test
    public void createCategoryByNameTest() throws Exception {
        category = new Category();
        PowerMockito.when(placeService, "createCategoryByName", ArgumentMatchers.any()).thenReturn(category);
    }

    @Test
    public void setPlaceToLocationTest() throws Exception {
//        when(locationService.findByLatAndLng(anyDouble(), anyDouble())).thenReturn(location);
//        when(locationService.findByLatAndLng(45.45, 45.456)).thenReturn(null);
//        when(locationService.save(location)).thenThrow(BadLocationRequestException.class);
//        PlaceServiceImpl placeServiceSpy = PowerMockito.spy(placeService);
//        PowerMockito.when(placeService, "setPlaceToLocation", ArgumentMatchers.any()).thenReturn();
//        Deencapsulation.invoke(placeService, "methodName", place);


    }

//    @Test
//    public void setPlaceToOpeningHours() {
//
//    }

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
        List<Place> places = new ArrayList<>();
        for (long i = 0; i < 3; i++) {
            Place place = Place.builder().id(i).status(PlaceStatus.PROPOSED).build();
            places.add(place);
        }
        when(placeRepo.findAllByStatusOrderByModifiedDateDesc(any())).thenReturn(places);
        List<AdminPlaceDto> foundList = placeService.getPlacesByStatus(PlaceStatus.PROPOSED);

        assertNotNull(foundList);
        for (AdminPlaceDto dto : foundList) {
            assertEquals(dto.getStatus(), PlaceStatus.PROPOSED);
            log.info(dto.toString());
        }
    }

    @Test
    public void getPlacesByNullStatusTest() {
        List<AdminPlaceDto> list = placeService.getPlacesByStatus(null);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test(expected = PlaceStatusException.class)
    public void updateStatusGivenTheSameStatusThenThrowException() {
        Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.save(any())).thenReturn(genericEntity);

        placeService.updateStatus(1L, PlaceStatus.PROPOSED);
    }

    @Test(expected = NotFoundException.class)
    public void updateStatusGivenPlaceIdNullThenThrowException() {
        Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

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
}

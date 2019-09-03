package greencity.service.impl;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import greencity.dto.location.MapBoundsDto;
import greencity.GreenCityApplication;
import greencity.dto.category.CategoryDto;
import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.Category;
import greencity.entity.Location;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.mapping.PlaceAddDtoMapper;
import greencity.repository.PlaceRepo;
import greencity.service.*;
import greencity.service.CategoryService;
import greencity.service.LocationService;
import greencity.service.OpenHoursService;
import greencity.service.UserService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class PlaceServiceImplTest {

    @Mock
    private PlaceRepo placeRepo;

    @Mock
    private CategoryService categoryService;

    @Mock
    private LocationService locationService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Place place;

    @InjectMocks
    private PlaceServiceImpl placeService;

    @Test
    public void save() {
        Category category = setCategoryToPlaceTest();

        Place place = new Place();
        place.setName("test");
        place.setCategory(category);
        Place expectedPlace = new Place();
        place.setId(1L);
        place.setName("test");
        place.setCategory(category);
        setPlaceToLocalionTest(expectedPlace);
        setPlaceToOpeningHoursTest(expectedPlace);
    }

    private Category setCategoryToPlaceTest() {
        Category category = new Category();
        category.setName("cafe");
        category.setId(1L);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("cafe");
        categoryService.save(categoryDto);
        return category;
    }

    private void setPlaceToOpeningHoursTest(Place expectedPlace) {
        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenTime(LocalTime.parse("10:30"));
        openingHours.setOpenTime(LocalTime.parse("20:30"));
        openingHours.setPlace(expectedPlace);
        OpeningHours expectedOpeningHours = new OpeningHours();
        openingHours.setId(1L);
        openingHours.setOpenTime(LocalTime.parse("10:30"));
        openingHours.setOpenTime(LocalTime.parse("20:30"));
        openingHours.setPlace(expectedPlace);
    }

    private void setPlaceToLocalionTest(Place expectedPlace) {
        Location location = new Location();
        location.setAddress("test address");
        location.setLat(45.456);
        location.setLng(46.456);
        location.setPlace(expectedPlace);
        locationService.save(location);
        Location expectedLocation = new Location();
        expectedLocation.setId(1L);
        location.setAddress("test address");
        location.setLat(45.456);
        location.setLng(46.456);
        location.setPlace(expectedPlace);
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

    @Test(expected = NullPointerException.class)
    public void updateStatusWhenOldStatusIsNullThrowException() {
        Place genericEntity = Place.builder().id(1L).status(null).build();

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        placeService.updateStatus(1L, PlaceStatus.DECLINED);
    }

    @Test
    public void getPlacesByStatusTest() {
        List<Place> gen = new ArrayList<>();
        List<AdminPlaceDto> genDto = new ArrayList<>();
        List<AdminPlaceDto> res = new ArrayList<>();
        assertEquals(res, genDto);
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
}

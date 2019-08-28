package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.dto.category.CategoryDto;
import greencity.dto.location.LocationDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceAddDto;
import greencity.entity.Category;
import greencity.entity.Location;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.mapping.PlaceAddDtoMapper;
import greencity.service.*;
import org.junit.Assert;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.repository.PlaceRepo;
import greencity.service.PlaceService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.ArgumentMatchers.anyLong;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class PlaceServiceImplTest {

    @MockBean private PlaceRepo placeRepo;

    @MockBean private ModelMapper modelMapper;

    @MockBean private CategoryService categoryService;

    @MockBean private LocationService locationService;

    @MockBean private OpenHoursService openHoursService;

    @MockBean private PlaceAddDtoMapper placeAddDtoMapper;

    @MockBean
    private UserService userService;

    @Autowired private PlaceService placeService;

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
        when(placeRepo.save(place)).thenReturn(expectedPlace);

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
        when(categoryService.findByName("cafe")).thenReturn(category);
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
        when(openHoursService.save(openingHours)).thenReturn(expectedOpeningHours);
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
        when(locationService.findById(1L)).thenReturn(expectedLocation);
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

        placeService.updateStatus(genericEntity.getId(), PlaceStatus.DECLINED);

        assertEquals(PlaceStatus.DECLINED, genericEntity.getStatus());
    }

    @Test
    public void getPlacesByStatusTest() {
        Category category = Category.builder().name("categoryName").build();
        List<AdminPlaceDto> foundList;
        List<Place> places = new ArrayList<>(3);
        for (long i = 0; i < 3; i++) {
            Place place =
                    Place.builder()
                            .id(i + 1)
                            .name("placeName" + i)
                            .description("placeDescription" + i)
                            .email("placeEmail@gmail.com" + i)
                            .phone("066034022" + i)
                            .modifiedDate(LocalDateTime.now().minusDays(i))
                            .status(PlaceStatus.PROPOSED)
                            .category(category)
                            .build();

            places.add(place);
        }

        when(placeRepo.findAllByStatusOrderByModifiedDateDesc(any())).thenReturn(places);
        foundList = placeService.getPlacesByStatus(PlaceStatus.PROPOSED);

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

        placeService.updateStatus(anyLong(), PlaceStatus.PROPOSED);
    }

    @Test(expected = NotFoundException.class)
    public void updateStatusGivenPlaceIdNullThenThrowException() {
        Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        placeService.updateStatus(null, PlaceStatus.DECLINED);
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
}

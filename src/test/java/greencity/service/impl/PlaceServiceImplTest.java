package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceStatusDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.mapping.PlaceAddDtoMapper;
import greencity.repository.PlaceRepo;
import greencity.service.CategoryService;
import greencity.service.LocationService;
import greencity.service.OpenHoursService;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

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

    @MockBean private UserService userService;

    @Autowired private PlaceService placeService;

    @Test
    public void deleteByIdTest() {
        Place placeToDelete = new Place();
        Mockito.when(placeRepo.findById(1L)).thenReturn(Optional.of(placeToDelete));

        Assert.assertEquals(true, placeService.deleteById(1L));
    }

    @Test
    public void updateStatusTest() {
        Place genericEntity = Place.builder().id(1L).status(PlaceStatus.PROPOSED).build();
        PlaceStatusDto genericDto = new PlaceStatusDto(1L, PlaceStatus.DECLINED);

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.save(any())).thenReturn(genericEntity);

        placeService.updateStatus(genericDto);

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
        PlaceStatusDto genericDto = new PlaceStatusDto(1L, PlaceStatus.PROPOSED);

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.save(any())).thenReturn(genericDto);

        placeService.updateStatus(genericDto);
    }

    @Test(expected = NotFoundException.class)
    public void updateStatusGivenPlaceIdNullThenThrowException() {
        Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();
        PlaceStatusDto genericDto = new PlaceStatusDto(null, PlaceStatus.DECLINED);

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        placeService.updateStatus(genericDto);
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

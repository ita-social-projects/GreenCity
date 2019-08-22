package greencity.service.impl;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.*;
import greencity.exception.BadIdException;
import greencity.exception.BadPlaceRequestException;
import greencity.repository.*;
import greencity.service.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlaceServiceImplTest {

    @Mock private PlaceRepo placeRepo;

    @Mock private CategoryService categoryService;

    @Mock private LocationService locationService;

    @Mock private OpeningHoursService openingHoursService;

    @Mock private UserService userService;

    private PlaceService placeService;

    @Before
    public void init() {
        placeService =
                new PlaceServiceImpl(
                        placeRepo,
                        categoryService,
                        locationService,
                        openingHoursService,
                        userService);
    }

    @Test
    public void deleteByIdTest() {
        Place placeToDelete = new Place();
        Mockito.when(placeRepo.findById(1L)).thenReturn(Optional.of(placeToDelete));

        Assert.assertEquals(true, placeService.deleteById(1L));
    }

    @Test
    public void findByIdTest() {
        Long id = 1L;

        Place expectedPlace = new Place();
        expectedPlace.setId(1L);

        when(placeRepo.findById(id)).thenReturn(Optional.of(expectedPlace));

        Place resultPlace = placeService.findById(id);

        assertEquals("IDs should be the same", id, resultPlace.getId());
        assertNotNull("PlaceAddress should be not null", resultPlace.getId());
    }

    @Test(expected = BadIdException.class)
    public void findByIdBadIdTest() {
        when(placeRepo.findById(any())).thenThrow(BadIdException.class);
        placeService.findById(1L);
    }
}

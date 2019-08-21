package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.repository.PlaceRepo;
import greencity.service.CategoryService;
import greencity.service.LocationService;
import greencity.service.PlaceService;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PlaceServiceImplTest {
    @Mock PlaceRepo placeRepo;
    private PlaceService placeService;
    @Autowired CategoryService categoryService;

    @Before
    public void init() {
        placeService = new PlaceServiceImpl(placeRepo);
    }

    @Test
    public void updateStatusTest() {
        Category category = Category.builder().name("categoryName").build();

        Place genericEntity =
                Place.builder()
                        .id(1L)
                        .name("placeName")
                        .description("placeDescription")
                        .email("placeEmail@gmail.com")
                        .phone("0973439892")
                        .status(PlaceStatus.PROPOSED)
                        .category(category)
                        .build();

        when(placeRepo.findById(any())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.saveAndFlush(any())).thenReturn(genericEntity);

        placeService.updateStatus(genericEntity.getId(), PlaceStatus.DECLINED);

        assertEquals(PlaceStatus.DECLINED, genericEntity.getStatus());
    }
}

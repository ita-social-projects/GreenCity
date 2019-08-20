package greencity.service.impl;

import static org.junit.Assert.*;

import greencity.GreenCityApplication;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.service.CategoryService;
import greencity.service.LocationService;
import greencity.service.PlaceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PlaceServiceImplTest {
    @Autowired private PlaceService placeService;
    @Autowired private CategoryService categoryService;
    @Autowired private LocationService locationService;

    @Test
    public void updateStatusTest() {
        Category category = Category.builder().name("categoryName").build();
        categoryService.save(category);

        Place genericEntity =
                Place.builder()
                        .name("placeName")
                        .description("placeDescription")
                        .email("placeEmail@gmail.com")
                        .phone("0973439892")
                        .status(PlaceStatus.PROPOSED)
                        .category(category)
                        .build();
        placeService.save(genericEntity);

        placeService.updateStatus(1L, PlaceStatus.DECLINED);
        Place foundEntity = placeService.findById(1L);

        assertEquals(PlaceStatus.DECLINED, foundEntity.getStatus());
    }
}

package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import greencity.GreenCityApplication;
import greencity.entity.Location;
import greencity.exception.NotFoundException;
import greencity.service.LocationService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LocationServiceImplTest {
    @Autowired private LocationService locationService;

    @Test
    public void findByIdTest() {
        Location genericEntity =
                Location.builder().lat(20.56).lng(45.89).address("Street, number1").build();
        locationService.save(genericEntity);

        Location foundEntity = locationService.findById(genericEntity.getId());

        assertNotNull(foundEntity);
        assertEquals(genericEntity, foundEntity);
    }

    @Test
    public void saveTest() {
        Location genericEntity =
                Location.builder().lat(20.56).lng(45.89).address("Street, number1").build();
        locationService.save(genericEntity);

        Location foundEntity = locationService.findById(1L);

        assertEquals(genericEntity, foundEntity);
    }

    @Test
    public void updateTest() {
        Location genericEntity =
                Location.builder().lat(20.56).lng(45.89).address("Street, number1").build();
        locationService.save(genericEntity);

        Location updated =
                Location.builder().id(1L).lat(20.56).lng(45.89).address("Street, number8").build();
        locationService.update(1L, updated);

        Location foundEntity = locationService.findById(1L);

        assertEquals(updated, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void deleteTest() {
        Location genericEntity =
                Location.builder().lat(20.56).lng(45.89).address("Street, number1").build();
        locationService.save(genericEntity);
        locationService.deleteById(1L);

        locationService.findById(1L);
    }

    @Test
    public void findAllTest() {
        List<Location> genericEntities = new ArrayList<>();
        List<Location> foundEntities;

        Location location1 =
                Location.builder().lat(20.56).lng(45.89).address("Street, number1").build();
        Location location2 =
                Location.builder().lat(20.56).lng(45.89).address("Street, number2").build();

        genericEntities.add(location1);
        genericEntities.add(location2);

        locationService.save(location1);
        locationService.save(location2);

        foundEntities = locationService.findAll();

        assertNotNull(foundEntities);
        assertEquals(genericEntities, foundEntities);
    }
}

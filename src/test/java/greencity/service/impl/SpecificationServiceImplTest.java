package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.entity.Specification;
import greencity.exception.NotFoundException;
import greencity.repository.SpecificationRepo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class SpecificationServiceImplTest {
    @Mock
    private SpecificationRepo specificationRepo;

    @InjectMocks
    private SpecificationServiceImpl specificationService;

    @Test
    public void saveTest() {
        Specification genericEntity = new Specification();

        when(specificationRepo.save(genericEntity)).thenReturn(genericEntity);

        assertEquals(genericEntity, specificationService.save(genericEntity));
    }

    @Test
    public void findByIdTest() {
        Specification genericEntity = new Specification();

        when(specificationRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        Specification foundEntity = specificationService.findById(anyLong());

        assertEquals(genericEntity, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdGivenIdNullThenThrowException() {
        specificationService.findById(null);
    }

    @Test
    public void findAllTest() {
        List<Specification> genericEntities =
            new ArrayList<>(Arrays.asList(new Specification(), new Specification()));

        when(specificationRepo.findAll()).thenReturn(genericEntities);

        List<Specification> foundEntities = specificationService.findAll();

        assertEquals(genericEntities, foundEntities);
    }

    @Test
    public void deleteByIdTest() {
        when(specificationRepo.findById(anyLong())).thenReturn(Optional.of(new Specification()));

        assertEquals(new Long(1), specificationService.deleteById(1L));
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdGivenIdNullThenThrowException() {
        specificationService.deleteById(null);
    }

    @Test
    public void findByNameTest() {
        Specification genericEntity = new Specification();

        when(specificationRepo.findByName(anyString())).thenReturn(Optional.of(genericEntity));

        Optional<Specification> foundEntity = specificationService.findByName(anyString());

        assertEquals(genericEntity, foundEntity);
    }
}
package greencity.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import greencity.entity.Specification;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.SpecificationRepo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SpecificationServiceImplTest {
    @Mock
    private SpecificationRepo specificationRepo;

    @InjectMocks
    private SpecificationServiceImpl specificationService;

    @Test
    void saveTest() {
        Specification genericEntity = new Specification();

        when(specificationRepo.save(genericEntity)).thenReturn(genericEntity);

        assertEquals(genericEntity, specificationService.save(genericEntity));
    }

    @Test
    void findByIdTest() {
        Specification genericEntity = new Specification();

        when(specificationRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        Specification foundEntity = specificationService.findById(anyLong());

        assertEquals(genericEntity, foundEntity);
    }

    @Test
    void findByIdGivenIdNullThenThrowException() {
        assertThrows(NotFoundException.class, () -> {
            specificationService.findById(null);
        });
    }

    @Test
    void findAllTest() {
        List<Specification> genericEntities =
            new ArrayList<>(Arrays.asList(new Specification(), new Specification()));

        when(specificationRepo.findAll()).thenReturn(genericEntities);

        List<Specification> foundEntities = specificationService.findAll();

        assertEquals(genericEntities, foundEntities);
    }

    @Test
    void deleteByIdTest() {
        when(specificationRepo.findById(anyLong())).thenReturn(Optional.of(new Specification()));

        assertEquals(new Long(1), specificationService.deleteById(1L));
    }

    @Test
    void deleteByIdGivenIdNullThenThrowException() {
        assertThrows(NotFoundException.class, () -> {
            specificationService.deleteById(null);
        });
    }

    @Test
    void findByNameTest() {
        Specification genericEntity = new Specification();

        when(specificationRepo.findByName(anyString())).thenReturn(Optional.of(genericEntity));

        Specification foundEntity = specificationService.findByName(anyString());

        assertEquals(genericEntity, foundEntity);
    }
}

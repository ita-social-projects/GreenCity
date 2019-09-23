package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.entity.Discount;
import greencity.repository.DiscountRepo;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class DiscountServiceImplTest {
    @InjectMocks
    DiscountServiceImpl discountService;
    @Mock
    private DiscountRepo discountRepo;

    @Test
    public void saveTest() {
        Discount genericEntity = new Discount();

        when(discountRepo.save(genericEntity)).thenReturn(genericEntity);

        assertEquals(genericEntity, discountService.save(genericEntity));
    }

    @Test
    public void findByIdTest() {
        Discount genericEntity = new Discount();

        when(discountRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        Discount foundEntity = discountService.findById(anyLong());

        assertEquals(genericEntity, foundEntity);
    }
}
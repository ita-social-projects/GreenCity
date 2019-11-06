package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import greencity.entity.DiscountValue;
import greencity.exception.NotFoundException;
import greencity.repository.DiscountValuesRepo;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DiscountServiceImplTest {

    @Mock
    private DiscountValuesRepo discountValuesRepo;

    @InjectMocks
    private DiscountServiceImpl discountService;

    @Test
    public void save() {
        DiscountValue discountValue = new DiscountValue();
        when(discountValuesRepo.save(any(DiscountValue.class))).thenReturn(new DiscountValue());
        assertEquals(discountValue, discountService.save(discountValue));
    }

    @Test
    public void findById() {
        DiscountValue genericEntity = new DiscountValue();
        when(discountValuesRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        DiscountValue foundEntity = discountService.findById(anyLong());
        assertEquals(genericEntity, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdGivenIdNullThenThrowException() {
        discountService.findById(null);
    }

    @Test
    public void findAllByPlaceId() {
        Set<DiscountValue> genericSet = new HashSet<>();
        when(discountValuesRepo.findAllByPlaceId(anyLong())).thenReturn(genericSet);
        Set<DiscountValue> foundSet = discountService.findAllByPlaceId(anyLong());
        assertEquals(genericSet, foundSet);
    }

}
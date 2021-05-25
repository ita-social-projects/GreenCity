package greencity.service;

import greencity.dto.discount.DiscountValueVO;
import greencity.entity.DiscountValue;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.DiscountValuesRepo;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DiscountServiceImplTest {
    private final DiscountValueVO discountValueVO = DiscountValueVO.builder()
        .id(1L)
        .value(10)
        .build();
    private final DiscountValue discountValue = DiscountValue.builder()
        .id(1L)
        .value(10)
        .build();
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private DiscountValuesRepo discountValuesRepo;
    @InjectMocks
    private DiscountServiceImpl discountService;

    @Test
    void saveTest() {
        when(modelMapper.map(discountValueVO, DiscountValue.class)).thenReturn(discountValue);
        when(discountValuesRepo.save(discountValue)).thenReturn(discountValue);
        when(modelMapper.map(discountValue, DiscountValueVO.class)).thenReturn(discountValueVO);

        DiscountValueVO actual = discountService.save(discountValueVO);
        assertEquals(discountValueVO, actual);
    }

    @Test
    void findByIdTest() {
        when(discountValuesRepo.findById(1L)).thenReturn(Optional.of(discountValue));
        when(modelMapper.map(discountValue, DiscountValueVO.class)).thenReturn(discountValueVO);

        DiscountValueVO actual = discountService.findById(1L);
        assertEquals(discountValueVO, actual);
    }

    @Test
    void findByIdGivenIdNullThenThrowExceptionTest() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> discountService.findById(null));
    }

    @Test
    void findAllByPlaceIdTest() {
        Set<DiscountValue> discountValues = Collections.singleton(discountValue);
        Set<DiscountValueVO> discountValuesVO = Collections.singleton(discountValueVO);

        when(discountValuesRepo.findAllByPlaceId(1L)).thenReturn(discountValues);
        when(modelMapper.map(discountValues, new TypeToken<Set<DiscountValueVO>>() {
        }.getType())).thenReturn(discountValuesVO);

        Set<DiscountValueVO> actual = discountService.findAllByPlaceId(1L);
        assertEquals(discountValuesVO, actual);
    }

    @Test
    void deleteAllByPlaceIdTest() {
        doNothing().when(discountValuesRepo).deleteAllByPlaceId(1L);
        discountService.deleteAllByPlaceId(1L);
        verify(discountValuesRepo, times(1)).deleteAllByPlaceId(1L);
    }
}
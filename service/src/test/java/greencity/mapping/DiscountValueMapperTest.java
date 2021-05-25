package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.discount.DiscountValueDto;
import greencity.entity.DiscountValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DiscountValueMapperTest {
    @InjectMocks
    private DiscountValueMapper discountValueMapper;

    @BeforeEach
    void init() {
        discountValueMapper = new DiscountValueMapper();
    }

    @Test
    void convertTest() {
        DiscountValue expected = ModelUtils.getDiscountValue();
        DiscountValueDto actualDiscountValueDto = ModelUtils.getDiscountValueDto();

        assertEquals(expected, discountValueMapper.convert(actualDiscountValueDto));
    }
}

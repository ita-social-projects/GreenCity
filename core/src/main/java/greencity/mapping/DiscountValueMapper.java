package greencity.mapping;

import greencity.dto.discount.DiscountValueDto;
import greencity.entity.DiscountValue;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

/**
 * The class uses other {@code Autowired} mappers to convert
 * {@link DiscountValue} entity objects to {@link DiscountValueDto} dto objects
 * and vise versa.
 *
 * @author Kateryna Horokh
 */
@AllArgsConstructor
@Component
public class DiscountValueMapper extends AbstractConverter<DiscountValueDto, DiscountValue> {
    @Override
    protected DiscountValue convert(DiscountValueDto discountValueDto) {
        return DiscountValue.builder()
            .value(discountValueDto.getValue())
            .build();
    }
}

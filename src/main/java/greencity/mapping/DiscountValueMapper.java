package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.discount.DiscountValueDto;
import greencity.entity.DiscountValue;
import greencity.entity.Specification;
import greencity.exception.NotFoundException;
import greencity.service.SpecificationService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * The class uses other {@code Autowired} mappers to convert {@link DiscountValue} entity objects to {@link
 * DiscountValueDto} dto objects and vise versa.
 *
 * @author Kateryna Horokh
 */
@AllArgsConstructor
@Component
public class DiscountValueMapper implements MapperToEntity<DiscountValueDto, DiscountValue> {
    private ModelMapper modelMapper;
    private SpecificationService specificationService;

    @Override
    public DiscountValue convertToEntity(DiscountValueDto dto) {
        DiscountValue discount = modelMapper.map(dto, DiscountValue.class);
        Specification specification = specificationService.findByName(dto.getSpecification().getName())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.SPECIFICATION_NOT_FOUND_BY_NAME));
        discount.setSpecification(specification);
        return discount;
    }
}

package greencity.mapping;

import greencity.dto.category.CategoryVO;
import greencity.dto.discount.DiscountValueVO;
import greencity.dto.place.PlaceVO;
import greencity.dto.specification.SpecificationVO;
import greencity.entity.DiscountValue;
import greencity.entity.Place;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class DiscountValueVOMapper extends AbstractConverter<DiscountValue, DiscountValueVO> {
    /**
     * Method convert {@link DiscountValue} to {@link DiscountValueVO}.
     *
     * @return {@link DiscountValueVO}
     */
    @Override
    protected DiscountValueVO convert(DiscountValue source) {
        Place place = source.getPlace();

        CategoryVO categoryVO = CategoryVO.builder()
            .id(place.getCategory().getId())
            .name(place.getCategory().getName())
            .build();

        PlaceVO placeVO = PlaceVO.builder()
            .id(source.getId())
            .description(place.getDescription())
            .email(place.getEmail())
            .modifiedDate(place.getModifiedDate())
            .name(place.getName())
            .phone(place.getPhone())
            .authorId(place.getAuthor().getId())
            .category(categoryVO)
            .locationId(place.getLocation().getId()).build();

        SpecificationVO specificationVO = SpecificationVO.builder()
            .id(source.getSpecification().getId())
            .name(source.getSpecification().getName()).build();
        return DiscountValueVO.builder()
            .id(source.getId())
            .value(source.getValue())
            .placeVO(placeVO)
            .specificationVO(specificationVO).build();
    }
}

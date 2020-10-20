package greencity.dto.discount;

import greencity.dto.place.PlaceVO;
import greencity.dto.specification.SpecificationVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class DiscountValueVO {
    private Long id;
    private Integer value;
    private PlaceVO placeVO;
    private SpecificationVO specificationVO;
}

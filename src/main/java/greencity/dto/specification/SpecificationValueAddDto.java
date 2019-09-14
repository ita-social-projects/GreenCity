package greencity.dto.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecificationValueAddDto {
    private String value;
    private SpecificationAddDto specificationAddDto;
}

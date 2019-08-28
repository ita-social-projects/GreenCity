package greencity.dto.category;

import javax.validation.constraints.NotBlank;

import greencity.constant.ValidationConstants;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @NotBlank(message = ValidationConstants.EMPTY_NAME_OF_CATEGORY)
    private String name;
}

package greencity.dto.category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import greencity.constant.ValidationConstants;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @NotNull(message = ValidationConstants.EMPTY_NAME_OF_CATEGORY)
    @NotBlank(message = ValidationConstants.EMPTY_NAME_OF_CATEGORY)
    private String name;
}

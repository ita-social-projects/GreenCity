package greencity.dto.category;

import greencity.constant.ValidationConstants;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = ValidationConstants.CATEGORY_NAME_BAD_FORMED)
    @Length(
            min = ValidationConstants.CATEGORY_NAME_MIN_LENGTH,
            max = ValidationConstants.CATEGORY_NAME_MAX_LENGTH)
    private String name;
}

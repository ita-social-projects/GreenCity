package greencity.dto.category;

import greencity.constant.ValidationConstants;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = ValidationConstants.CATEGORY_NAME_BAD_FORMED)
    @Length(
        min = ValidationConstants.CATEGORY_NAME_MIN_LENGTH,
        max = ValidationConstants.CATEGORY_NAME_MAX_LENGTH)
    private String name;
}

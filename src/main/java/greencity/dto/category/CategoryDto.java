package greencity.dto.category;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CategoryDto {

    @NotBlank private String name;
}

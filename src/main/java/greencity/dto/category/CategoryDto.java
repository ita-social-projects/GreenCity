package greencity.dto.category;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class CategoryDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;
}

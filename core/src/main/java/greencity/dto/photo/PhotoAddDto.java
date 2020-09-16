package greencity.dto.photo;

import greencity.constant.ValidationConstants;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoAddDto {
    @NotBlank
    private String name;
}

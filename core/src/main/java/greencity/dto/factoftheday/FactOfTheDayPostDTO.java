package greencity.dto.factoftheday;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactOfTheDayPostDTO {
    @NotNull
    private Long id;
    @Size(min = 1, max = 300)
    private String name;
    @Valid
    private List<FactOfTheDayTranslationEmbeddedPostDTO> factOfTheDayTranslations;
}

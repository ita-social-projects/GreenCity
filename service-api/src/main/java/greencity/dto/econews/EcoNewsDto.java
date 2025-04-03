package greencity.dto.econews;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EcoNewsDto extends BaseEcoNewsDto {
    @NotEmpty
    private List<String> tagsEn;

    @NotEmpty
    private List<String> tagsUk;
}

package greencity.dto.habittranslation;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitTranslationManagementDto implements Serializable {
    private Long id;
    @NotEmpty
    @Size(min = 1, max = 170)
    private String name;
    @NotEmpty
    @Size(min = 1, max = 170)
    private String habitItem;
    @NotEmpty
    @Size(min = 1, max = 170)
    private String description;
    @NotEmpty
    private String languageCode;
}

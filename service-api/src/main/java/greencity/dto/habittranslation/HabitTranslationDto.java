package greencity.dto.habittranslation;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class HabitTranslationDto implements Serializable {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String habitItem;
    @NotBlank
    private String languageCode;
}

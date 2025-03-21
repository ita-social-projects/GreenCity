package greencity.dto.habittranslation;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitTranslationDto implements Serializable {
    @NotBlank
    private String descriptionEn;
    private String habitItemEn;
    @NotBlank
    private String languageCode;
    @NotBlank
    private String nameEn;
    private String descriptionUk;
    private String nameUk;
    private String habitItemUk;
}

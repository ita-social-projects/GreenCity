package greencity.dto.language;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Data;
import lombok.Builder;

@EqualsAndHashCode(of = {"id", "code"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageDTO {
    @Min(1)
    private Long id;

    @NotNull
    private String code;
}

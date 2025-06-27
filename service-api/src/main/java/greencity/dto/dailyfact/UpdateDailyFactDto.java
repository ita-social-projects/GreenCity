package greencity.dto.dailyfact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UpdateDailyFactDto {
    @NotNull
    @Positive
    Long id;

    @NotBlank
    @Length(max = 300)
    private String factEn;

    @NotBlank
    @Length(max = 300)
    private String factUk;
}

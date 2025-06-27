package greencity.dto.dailyfact;

import greencity.constant.ServiceValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UpdateDailyFactDto {
    @NotNull
    @Positive
    Long id;

    @Email(regexp = ServiceValidationConstants.EMAIL_REGEXP)
    private String email;

    @NotBlank
    @Length(max = 300)
    private String factEn;

    @NotBlank
    @Length(max = 300)
    private String factUk;
}

package greencity.dto.dailyfact;

import greencity.constant.ServiceValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class AddDailyFactDto {
    @Email(regexp = ServiceValidationConstants.EMAIL_REGEXP)
    private String email;

    @NotBlank
    @Length(max = 300)
    private String factEn;

    @NotBlank
    @Length(max = 300)
    private String factUk;
}

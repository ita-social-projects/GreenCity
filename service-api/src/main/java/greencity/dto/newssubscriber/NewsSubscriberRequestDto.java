package greencity.dto.newssubscriber;

import greencity.constant.AppConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsSubscriberRequestDto {
    @NotBlank
    @Email(regexp = AppConstant.VALIDATION_EMAIL)
    private String email;
}

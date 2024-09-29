package greencity.dto.subscription;

import greencity.constant.AppConstant;
import greencity.enums.SubscriptionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequestDto {
    @NotBlank
    @Email(regexp = AppConstant.VALIDATION_EMAIL)
    private String email;

    @NotNull
    private SubscriptionType subscriptionType;
}

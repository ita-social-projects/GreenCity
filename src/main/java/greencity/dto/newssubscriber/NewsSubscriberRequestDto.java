package greencity.dto.newssubscriber;

import static greencity.constant.AppConstant.VALIDATION_EMAIL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsSubscriberRequestDto {
    @NotBlank
    @Email(regexp = VALIDATION_EMAIL)
    private String email;
}

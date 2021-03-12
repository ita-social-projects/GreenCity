package greencity.dto.newssubscriber;

import static greencity.constant.AppConstant.VALIDATION_EMAIL;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsSubscriberResponseDto implements Serializable {
    @NotBlank
    @Email(regexp = VALIDATION_EMAIL)
    private String email;
    @NotBlank
    private String unsubscribeToken;
}

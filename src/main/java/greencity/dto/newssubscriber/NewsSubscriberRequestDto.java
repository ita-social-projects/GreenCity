package greencity.dto.newssubscriber;

import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsSubscriberRequestDto {
    @Email
    public String email;
}

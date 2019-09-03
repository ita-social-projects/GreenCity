package greencity.dto.userownsecurity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccessTokenDto {
    private String accessToken;
}

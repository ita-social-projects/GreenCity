package greencity.dto.user;

import java.io.Serializable;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EcoNewsAuthorDto implements Serializable {
    private Long id;
    private String name;
}


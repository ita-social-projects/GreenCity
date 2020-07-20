package greencity.dto.user;

import java.io.Serializable;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TipsAndTricksAuthorDto implements Serializable {
    private Long id;
    private String name;
}
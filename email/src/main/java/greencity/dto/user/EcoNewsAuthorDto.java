package greencity.dto.user;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EcoNewsAuthorDto implements Serializable {
    private Long id;
    private String name;
}
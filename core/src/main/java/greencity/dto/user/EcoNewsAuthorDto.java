package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EcoNewsAuthorDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
}

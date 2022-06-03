package greencity.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
@Builder
public class NewTagDto {
    private Long id;
    private String name;
    private String nameUa;
}

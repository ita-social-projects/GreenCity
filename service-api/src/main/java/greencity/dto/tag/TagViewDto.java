package greencity.dto.tag;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class TagViewDto {
    private String id;
    private String type;
    private String name;
}

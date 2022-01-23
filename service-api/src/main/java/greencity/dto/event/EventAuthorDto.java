package greencity.dto.event;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventAuthorDto {
    private Long id;
    private String name;
}

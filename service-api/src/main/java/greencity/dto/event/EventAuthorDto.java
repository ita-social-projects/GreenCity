package greencity.dto.event;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public final class EventAuthorDto {
    private Long id;
    private String name;
}

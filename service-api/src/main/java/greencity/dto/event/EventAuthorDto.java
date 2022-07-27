package greencity.dto.event;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class EventAuthorDto {
    private Long id;
    private String name;
    private Double organizerRating;
}

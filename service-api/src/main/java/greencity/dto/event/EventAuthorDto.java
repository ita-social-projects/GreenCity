package greencity.dto.event;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Builder;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class EventAuthorDto {
    private Long id;
    private String name;
    private Double organizerRating;
    private String email;
}

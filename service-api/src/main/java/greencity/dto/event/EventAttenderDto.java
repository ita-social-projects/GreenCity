package greencity.dto.event;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class EventAttenderDto {
    private Long id;
    private String name;
    private String imagePath;
}

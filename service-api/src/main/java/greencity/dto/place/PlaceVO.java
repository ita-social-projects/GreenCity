package greencity.dto.place;

import greencity.enums.PlaceStatus;
import java.time.ZonedDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class PlaceVO {
    private Long id;
    private String description;
    private String email;
    private ZonedDateTime modifiedDate;
    private String name;
    private String phone;
    private PlaceStatus status = PlaceStatus.PROPOSED;
    private Long authorId;
    private Long categoryId;
    private Long locationId;
}

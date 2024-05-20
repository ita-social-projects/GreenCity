package greencity.dto.event;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Setter
public class EventDateLocationPreviewDto {
    private ZonedDateTime startDate;

    private ZonedDateTime finishDate;

    private String onlineLink;

    private AddressDto coordinates;
}

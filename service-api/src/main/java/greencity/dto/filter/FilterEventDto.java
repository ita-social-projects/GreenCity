package greencity.dto.filter;

import greencity.enums.EventStatus;
import greencity.enums.EventTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterEventDto {
    private List<EventTime> eventTime;
    private List<String> cities;
    private List<EventStatus> statuses;
    private List<String> tags;
}

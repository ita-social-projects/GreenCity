package greencity.dto.filter;

import greencity.enums.EventStatus;
import greencity.enums.EventTime;
import greencity.enums.EventType;
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
    private EventTime eventTime;
    private List<String> cities;
    private List<EventStatus> statuses;
    private List<String> tags;
    private String title;
    private EventType eventType;
    private Long userId;
}

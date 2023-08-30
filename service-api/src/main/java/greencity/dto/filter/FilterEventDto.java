package greencity.dto.filter;

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
    private List<String> eventTime;
    private List<String> cities;
    private List<String> statuses;
    private List<String> tags;
}

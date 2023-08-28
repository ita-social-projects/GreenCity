package greencity.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterEventDto {
    private String[] eventTime;
    private String[] cities;
    private String[] statuses;
    private String[] tags;
}

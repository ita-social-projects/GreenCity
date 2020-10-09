package greencity.dto.tipsandtricks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class TipsAndTricksViewDto {
    private String id;
    private String title;
    private String author;
    private String startDate;
    private String endDate;
}

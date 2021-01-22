package greencity.dto.econews;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class EcoNewsViewDto {
    private String id;
    private String title;
    private String author;
    private String text;
    private String startDate;
    private String endDate;
    private String tags;
}

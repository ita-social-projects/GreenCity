package greencity.dto.econews;

import lombok.*;

import java.util.List;

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
    private String imagePath;
    private String source;
    private String tags;
}

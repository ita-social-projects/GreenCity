package greencity.dto.econews;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class EcoNewsViewDto {
    private String id;
    private String title;
    private String author;
    private String text;
    private String startDate;
    private String endDate;
    private String tags;

    /**
     * This method check if object is empty.
     */
    public boolean isEmpty() {
        return id.isEmpty() && title.isEmpty() && author.isEmpty() && text.isEmpty()
            && startDate.isEmpty()
            && endDate.isEmpty() && tags.isEmpty();
    }
}

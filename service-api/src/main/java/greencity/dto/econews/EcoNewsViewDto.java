package greencity.dto.econews;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class EcoNewsViewDto {
    private String id = "";
    private String title = "";
    private String author = "";
    private String text = "";
    private String startDate = "";
    private String endDate = "";
    private String tags = "";

    /**
     * This method check if object is empty.
     */
    public boolean isEmpty() {
        return id.isEmpty() && title.isEmpty() && author.isEmpty() && text.isEmpty()
            && startDate.isEmpty()
            && endDate.isEmpty() && tags.isEmpty();
    }
}

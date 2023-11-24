package greencity.dto.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class EventViewDto {
    private String id;
    private String author;
    private String title;
    private String description;
    private String tags;

    /**
     * This method check if object is empty.
     */
    public boolean isEmpty() {
        return id.isEmpty() && title.isEmpty() && author.isEmpty() && description.isEmpty() && tags.isEmpty();
    }
}

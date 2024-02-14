package greencity.dto.event;

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

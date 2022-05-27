package greencity.dto.event;

import greencity.dto.tag.TagUaEnDto;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class EventDto {
    private Long id;

    private String title;

    private EventAuthorDto organizer;

    private String description;

    @Max(7)
    private List<EventDateLocationDto> dates;

    @NotEmpty
    private List<TagUaEnDto> tags;

    @Nullable
    private String titleImage;

    @Nullable
    @Max(4)
    private List<String> additionalImages;

    private boolean isOpen;
}

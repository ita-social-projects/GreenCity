package greencity.dto.event;

import greencity.dto.tag.TagUaEnDto;
import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
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

    private LocalDate creationDate;

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

    private Boolean isSubscribed;

    private Boolean isFavorite;

    /**
     * Return String of event tags in English.
     *
     */
    public String tagsToStringEn() {
        if (!CollectionUtils.isEmpty(tags)) {
            var ref = new Object() {
                String tagsEn = "";
            };
            tags.forEach(t -> ref.tagsEn += t.getNameEn() + ", ");
            ref.tagsEn = ref.tagsEn.substring(0, ref.tagsEn.length() - 2);
            return ref.tagsEn;
        }
        return "";
    }
}

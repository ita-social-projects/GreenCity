package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.user.AuthorDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class EventPreviewDto {
    private Long id;

    private String title;

    private AuthorDto organizer;

    private LocalDate creationDate;

    private Set<EventDateLocationPreviewDto> dates;

    private List<TagUaEnDto> tags;

    private String titleImage;

    @JsonProperty("open")
    private Boolean isOpen;

    private Boolean isSubscribed;

    private Boolean isFavorite;

    private Boolean isRelevant;

    private Long likes;

    private Long countComments;

    private Boolean isOrganizedByFriend;

    private Double eventRate;
}

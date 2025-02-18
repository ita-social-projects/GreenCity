package greencity.dto.econews;

import greencity.annotations.SortableField;
import greencity.dto.SortableDTO;
import greencity.dto.user.EcoNewsAuthorDto;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "author")
@Builder
@EqualsAndHashCode
public class EcoNewsGenericDto implements SortableDTO {
    @Min(1)
    @SortableField
    private Long id;

    @NotEmpty
    @SortableField
    private String title;

    @NotEmpty
    private String content;

    private String shortInfo;

    @NotEmpty
    private EcoNewsAuthorDto author;

    @NotEmpty
    @SortableField
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;

    @NotEmpty
    private String imagePath;

    private String source;

    @NotEmpty
    private List<String> tagsUa;

    @NotEmpty
    private List<String> tagsEn;

    @SortableField
    private int likes;

    @SortableField
    private int countComments;

    private int countOfEcoNews;

    private boolean isFavorite;
}

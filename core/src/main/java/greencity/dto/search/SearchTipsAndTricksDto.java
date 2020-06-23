package greencity.dto.search;

import greencity.dto.user.AuthorDto;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SearchTipsAndTricksDto {
    @Min(1)
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private AuthorDto author;

    @NotEmpty
    private ZonedDateTime creationDate;

    @NotEmpty
    private List<String> tipsAndTricksTags;
}

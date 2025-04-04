package greencity.dto.econews;

import greencity.dto.user.EcoNewsAuthorDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "author")
@SuperBuilder
@EqualsAndHashCode
public class BaseEcoNewsDto {
    @NotNull
    private ZonedDateTime creationDate;

    @NotEmpty
    private String imagePath;

    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    private String shortInfo;

    @NotNull
    private EcoNewsAuthorDto author;

    private int likes;

    private int dislikes;

    private int countComments;

    private boolean hidden;
}

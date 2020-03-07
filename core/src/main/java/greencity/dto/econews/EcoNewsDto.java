package greencity.dto.econews;

import greencity.dto.user.EcoNewsAuthorDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EcoNewsDto {
    @NotNull
    @Min(0)
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String text;
    @NotEmpty
    private String imagePath;
    @NotEmpty
    private EcoNewsAuthorDto ecoNewsAuthorDto;
    @NotEmpty
    private ZonedDateTime creationDate;
}

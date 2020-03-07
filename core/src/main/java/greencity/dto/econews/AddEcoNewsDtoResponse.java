package greencity.dto.econews;

import greencity.dto.user.EcoNewsAuthorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddEcoNewsDtoResponse implements Serializable {
    @Min(1)
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String text;
    @NotEmpty
    private EcoNewsAuthorDto ecoNewsAuthorDto;
    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;
    @NotEmpty
    private String imagePath;
}

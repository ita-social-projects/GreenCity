package greencity.dto.econews;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static greencity.constant.ValidationConstants.MIN_AMOUNT_OF_TAGS;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UpdateEcoNewsDto {
    @NotNull
    @Min(1)
    private Long id;

    @Size(min = 1, max = 170)
    private String title;

    @Size(min = 20, max = 63206)
    private String text;

    @NotEmpty(message = MIN_AMOUNT_OF_TAGS)
    private List<String> tags;

    private String image;

    private String source;
}

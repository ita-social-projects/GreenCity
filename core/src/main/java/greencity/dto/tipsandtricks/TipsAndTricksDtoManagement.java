package greencity.dto.tipsandtricks;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import static greencity.constant.AppConstant.VALIDATION_EMAIL;
import static greencity.constant.ValidationConstants.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TipsAndTricksDtoManagement implements Serializable {
    @NotNull
    @Min(1)
    private Long id;

    @Size(min = 1, max = 170)
    private String title;

    @Size(min = 20, max = 63206)
    private String text;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;

    @NotBlank
    @Email(regexp = VALIDATION_EMAIL, message = INVALID_EMAIL)
    private String emailAuthor;

    @NotEmpty(message = MIN_AMOUNT_OF_TAGS)
    private List<String> tags;

    private String imagePath;

    private String source;
}

package greencity.dto.comment;

import greencity.constant.ValidationConstants;
import greencity.dto.photo.PhotoAddDto;
import greencity.dto.rate.RateAddDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentDto {
    @NotBlank(message = ValidationConstants.EMPTY_COMMENT)
    @Length(min = ValidationConstants.COMMENT_MIN_LENGTH, max = ValidationConstants.COMMENT_MAX_LENGTH)
    private String text;
    @Valid
    private RateAddDto rate;
    @Valid
    private List<PhotoAddDto> photos;
}

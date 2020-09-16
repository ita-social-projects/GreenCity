package greencity.dto.comment;

import greencity.constant.ValidationConstants;
import greencity.dto.photo.PhotoAddDto;
import greencity.dto.rate.EstimateAddDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentDto {
    @NotBlank
    @Length(min = ValidationConstants.COMMENT_MIN_LENGTH, max = ValidationConstants.COMMENT_MAX_LENGTH)
    private String text;
    @Valid
    private EstimateAddDto estimate;
    @Valid
    @Size(max = 3, message = ValidationConstants.BAD_PHOTO_LIST_REQUEST)
    private List<PhotoAddDto> photos;
}

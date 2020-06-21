package greencity.dto.econewscomment;

import greencity.constant.ValidationConstants;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddEcoNewsCommentDtoRequest {
    @NotBlank(message = ValidationConstants.EMPTY_COMMENT)
    @Length(min = 1, max = 8000)
    private String text;

    private Long parentCommentId;
}

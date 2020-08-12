package greencity.dto.econewscomment;

import greencity.constant.ValidationConstants;
import javax.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AddEcoNewsCommentDtoRequest {
    @NotBlank(message = ValidationConstants.EMPTY_COMMENT)
    @Length(min = 1, max = 8000)
    private String text;

    private Long parentCommentId;
}

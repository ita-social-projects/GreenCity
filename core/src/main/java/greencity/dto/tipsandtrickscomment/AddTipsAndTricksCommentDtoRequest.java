package greencity.dto.tipsandtrickscomment;

import javax.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AddTipsAndTricksCommentDtoRequest {
    @NotBlank
    @Length(min = 1, max = 8000)
    private String text;

    private Long parentCommentId;
}

package greencity.dto.econewscomment;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AddEcoNewsCommentDtoRequest {
    @NotBlank(message = "The text of comment can not be empty")
    @Length(min = 1, max = 8000)
    private String text;

    private Long parentCommentId;
}

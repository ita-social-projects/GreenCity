package greencity.dto.eventcomment;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EditEventCommentDtoRequest {
    @NotBlank(message = "The text of comment can not be empty")
    @Length(min = 1, max = 8000)
    private String newText;
}

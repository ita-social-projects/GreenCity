package greencity.dto.eventcomment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UpdateEventCommentDtoRequest {
    @NotBlank(message = "The text of comment can not be empty")
    @Length(min = 1, max = 8000)
    private String text;
}

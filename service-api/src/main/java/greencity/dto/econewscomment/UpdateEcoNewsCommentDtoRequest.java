package greencity.dto.econewscomment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UpdateEcoNewsCommentDtoRequest {
    @NotBlank(message = "The text of comment can not be empty")
    @Size(min = 1, max = 100)
    public String text;
}
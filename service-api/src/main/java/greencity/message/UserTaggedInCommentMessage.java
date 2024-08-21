package greencity.message;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class UserTaggedInCommentMessage extends CommentMessage implements Serializable {
    @NotEmpty(message = "Tagger name cannot be empty")
    private String taggerName;
}

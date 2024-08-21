package greencity.message;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
public abstract class CommentMessage implements Serializable {
    @NotEmpty(message = "Receiver name cannot be empty")
    private String receiverName;

    @NotEmpty(message = "Receiver email cannot be empty")
    private String receiverEmail;

    @NotEmpty(message = "Language cannot be empty")
    private String language;

    @NotEmpty(message = "Name of element cannot be null")
    private String elementName;

    @NotEmpty(message = "Comment cannot be empty")
    private String commentText;

    @NotNull(message = "Date cannot be null")
    private LocalDateTime creationDate;

    @NotNull(message = "Element ID cannot be null")
    private Long commentedElementId;

    @NotEmpty(message = "Base link cannot be null")
    private String baseLink;
}

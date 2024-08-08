package greencity.message;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTaggedInCommentMessage implements Serializable {
    @NotEmpty(message = "Tagger name cannot be empty")
    private String taggerName;

    @NotEmpty(message = "Receiver name cannot be empty")
    private String receiverName;

    @NotEmpty(message = "Receiver email cannot be empty")
    private String receiverEmail;

    @NotEmpty(message = "Language cannot be empty")
    private String language;

    @NotEmpty(message = "Name of event cannot be null")
    private String eventName;

    @NotEmpty(message = "Comment cannot be empty")
    private String commentText;

    @NotNull(message = "Date cannot be null")
    private LocalDateTime creationDate;

    @NotNull(message = "Event ID cannot be null")
    private Long commentedEventId;
}

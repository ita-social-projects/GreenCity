package greencity.message;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class HabitAssignNotificationMessage implements Serializable {
    @NotEmpty(message = "Sender name cannot be empty")
    private String senderName;

    @NotEmpty(message = "Receiver name cannot be empty")
    private String receiverName;

    @NotEmpty(message = "Receiver email cannot be empty")
    @Email(message = "Receiver email should be valid")
    private String receiverEmail;

    @NotEmpty(message = "Habit name cannot be empty")
    private String habitName;

    @NotEmpty(message = "Language cannot be empty")
    private String language;

    @NotNull(message = "Habit assign ID cannot be null")
    private Long habitAssignId;
}

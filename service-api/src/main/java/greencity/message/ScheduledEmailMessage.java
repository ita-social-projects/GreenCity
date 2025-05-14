package greencity.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduledEmailMessage {
    private String username;
    private Long userId;
    private String baseLink;
    private String subject;
    private String body;
    private String language;
    private boolean isUbs;
}

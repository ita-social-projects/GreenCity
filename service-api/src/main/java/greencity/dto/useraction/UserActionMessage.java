package greencity.dto.useraction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import greencity.enums.ActionContextType;
import greencity.enums.UserActionType;
import lombok.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserActionMessage {
    private String userEmail;

    private UserActionType actionType;

    private ActionContextType contextType;

    private Long contextId;

    private ZonedDateTime timestamp;

    /**
     * Used for serialization of {@code timestamp} field.
     */
    @JsonGetter("timestamp")
    public String getTimestampAsString() {
        return DateTimeFormatter.ISO_INSTANT.format(timestamp);
    }

    /**
     * Used for deserialization of {@code timestamp} field.
     */
    @JsonSetter("timestamp")
    public void setTimestampFromString(String timestamp) {
        this.timestamp = ZonedDateTime.parse(timestamp);
    }
}

package greencity.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SendEventCreationNotification.class, name = "eventCreation"),
    @JsonSubTypes.Type(value = NewsCommentMessage.class, name = "newsCommented"),
    @JsonSubTypes.Type(value = EventCommentedMessage.class, name = "eventCommented")
})
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractEmailMessage implements Serializable {
    private String email;
    private String name;
    private String subject;
    private String message;
}

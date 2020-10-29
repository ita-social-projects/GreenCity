package greencity.message;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public final class SendChangePlaceStatusEmailMessage implements Serializable {
    private String authorFirstName;
    private String placeName;
    private String placeStatus;
    private String authorEmail;
}

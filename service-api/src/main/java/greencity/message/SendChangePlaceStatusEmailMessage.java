package greencity.message;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Builder
public final class SendChangePlaceStatusEmailMessage implements Serializable {
    private String authorFirstName;
    private String placeName;
    private String placeStatus;
    private String authorEmail;
}

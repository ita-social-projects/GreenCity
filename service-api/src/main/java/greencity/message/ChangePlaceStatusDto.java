package greencity.message;

import greencity.enums.PlaceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class ChangePlaceStatusDto implements EmailMessage {
    private String authorFirstName;
    private String authorLanguage;
    private String placeName;
    private PlaceStatus placeStatus;
    private String authorEmail;

    @Override
    public String getEmail() {
        return this.getAuthorEmail();
    }
}

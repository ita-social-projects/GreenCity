package greencity.dto.econews;

import greencity.dto.user.PlaceAuthorDto;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EcoNewsForSendEmailDto {
    private String unsubscribeToken;
    private ZonedDateTime creationDate;
    private String imagePath;
    private String source;
    private PlaceAuthorDto author;
    private String title;
    private String text;
}

package greencity.dto.place;

import greencity.dto.location.LocationDto;
import greencity.dto.openhours.OpenHoursDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.enums.PlaceStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPlaceDto {

    private Long id;
    private String name;
    private LocationDto location;
    private List<OpenHoursDto> openingHours;
    private PlaceAuthorDto author;
    private LocalDateTime modifiedDate;
    private PlaceStatus status;
}

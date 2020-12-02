package greencity.dto.place;

import com.fasterxml.jackson.annotation.JsonFormat;
import greencity.dto.category.CategoryDto;
import greencity.dto.location.LocationDto;
import greencity.dto.openhours.OpenHoursDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.enums.PlaceStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AdminPlaceDto {
    private Long id;
    private String name;
    private LocationDto location;
    private CategoryDto category;
    private List<OpenHoursDto> openingHoursList;
    private PlaceAuthorDto author;
    private PlaceStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime modifiedDate;
}

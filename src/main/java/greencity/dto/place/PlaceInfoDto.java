package greencity.dto.place;

import greencity.dto.comment.CommentDto;
import greencity.dto.location.LocationDto;
import greencity.dto.openhours.OpenHoursDto;
import greencity.dto.specification.SpecificationValueDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceInfoDto {
    private Long id;
    private String name;
    private LocationDto location;
    private List<OpenHoursDto> openingHoursList;
    private List<SpecificationValueDto> specificationValues;
    private List<CommentDto> comments;
    private Double rate;
}

package greencity.dto.place;

import greencity.dto.category.CategoryDto;
import greencity.dto.location.LocationDto;
import greencity.dto.openingHours.OpeningHoursDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceAddDto {

    @NotBlank
    @Length(max = 30)
    private String name;

    @NotNull private LocationDto location;

    @NotBlank
    @Length(max = 30)
    private String address;

    private String categoryName;

    private Long categoryId;

    @NotNull private Long authorId;

    @NotNull private List<OpeningHoursDto> openingHoursDtoList;

    @NotNull private PlaceStatus placeStatus = PlaceStatus.PROPOSED;
}

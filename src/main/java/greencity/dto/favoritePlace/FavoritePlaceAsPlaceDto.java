package greencity.dto.favoritePlace;

import greencity.dto.location.LocationDto;
import greencity.dto.openingHours.OpeningHoursDto;
import greencity.entity.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
//
//@Data
//public class FavoritePlaceAsPlaceDto {
//    @NotBlank
//    @Length(max = 30)
//    private String name;
//    @NotBlank
//    private String address;
//    @NotNull
//    private LocationDto location;
//    private List<OpeningHoursDto> openingHours;
//    private int commentsCount;
//    private Photo photo;
//    private double rate;
//}

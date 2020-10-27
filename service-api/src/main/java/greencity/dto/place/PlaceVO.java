package greencity.dto.place;

import greencity.dto.category.CategoryVO;
import greencity.dto.descountvalue.DiscountValueVO;
import greencity.dto.location.LocationVO;
import greencity.dto.openhours.OpeningHoursVO;
import greencity.dto.photo.PhotoVO;
import greencity.dto.user.UserVO;
import greencity.enums.PlaceStatus;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString(
    exclude = {"discountValues", "author", "photos",
        "location", "category", "status", "discountValues"}
)
public class PlaceVO {
    private Long id;
    private String description;
    private String email;
    private ZonedDateTime modifiedDate;
    private String name;
    private String phone;
    private PlaceStatus status = PlaceStatus.PROPOSED;
    private CategoryVO category;
    private List<PhotoVO> photos = new ArrayList<>();
    private Set<DiscountValueVO> discountValues = new HashSet<>();
    private Set<OpeningHoursVO> openingHoursList = new HashSet<>();
    private LocationVO location;
    private UserVO author;
}

package greencity.dto.place;

import greencity.dto.category.CategoryVO;
import greencity.dto.descountvalue.DiscountValueVO;
import greencity.dto.photo.PhotoVO;
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
@ToString
public class PlaceVO {
    private Long id;
    private String description;
    private String email;
    private ZonedDateTime modifiedDate;
    private String name;
    private String phone;
    private PlaceStatus status = PlaceStatus.PROPOSED;
    private Long authorId;
    private CategoryVO category;
    private Long locationId;
    private List<PhotoVO> photos = new ArrayList<>();
    private Set<DiscountValueVO> discountValues = new HashSet<>();
}

package greencity.dto.placecomment;

import greencity.annotations.SortableField;
import greencity.dto.SortableDTO;
import greencity.dto.photo.PhotoReturnDto;
import greencity.dto.place.AdminPlaceDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCommentAdminDto implements SortableDTO {
    @SortableField
    private Long id;

    private String text;

    @SortableField
    private LocalDateTime createdDate;

    private List<PhotoReturnDto> photos;

    private AdminPlaceDto place;
}

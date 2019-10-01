package greencity.dto.comment;

import greencity.dto.photo.PhotoReturnDto;
import greencity.dto.rate.RateAddDto;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReturnDto {
    private Long id;
    private String text;
    private LocalDate date;
    private List<PhotoReturnDto> photos;
    private RateAddDto rate;
//    private Long placeId;
//    private Long userId;
}

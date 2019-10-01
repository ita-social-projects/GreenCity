package greencity.dto.comment;

import greencity.dto.photo.PhotoAddDto;
import greencity.dto.rate.RateAddDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentDto {
    private String text;
    private RateAddDto rate;
    private List<PhotoAddDto> photos;
}

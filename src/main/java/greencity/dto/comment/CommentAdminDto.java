package greencity.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CommentAdminDto {
    private Long id;
    private String text;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    private List<PhotoReturnDto> photos;
    private AdminPlaceDto place;
}

package greencity.dto.comment;

import greencity.dto.user.UserForListDto;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long place;
    private Long id;
    private String text;
    private LocalDate date;
    private UserForListDto user;
}

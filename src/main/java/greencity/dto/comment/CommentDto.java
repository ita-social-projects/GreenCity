package greencity.dto.comment;

import greencity.dto.user.UserForListDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private LocalDate date;
    private UserForListDto user;
}

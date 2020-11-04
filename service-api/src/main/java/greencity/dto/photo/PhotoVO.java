package greencity.dto.photo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoVO {
    private Long id;
    private String name;
    private Long commentId;
    private Long placeId;
    private Long userId;
}

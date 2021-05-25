package greencity.dto.photo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class PhotoVO {
    private Long id;
    private String name;
    private Long commentId;
    private Long placeId;
    private Long userId;
}

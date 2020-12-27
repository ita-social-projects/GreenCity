package greencity.dto.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RecommendedFriendDto {
    private Long id;
    private String name;
    private String profilePicture;
}

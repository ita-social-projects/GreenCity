package greencity.dto;

import greencity.enums.UserStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ParticipantDto {
    private Long id;
    private String name;
    private String email;
    private String profilePicture;
    private UserStatus userStatus;
}

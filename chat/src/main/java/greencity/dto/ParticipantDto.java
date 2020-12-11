package greencity.dto;

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
}

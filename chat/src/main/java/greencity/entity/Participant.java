package greencity.entity;

import greencity.enums.UserStatus;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "users")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Enumerated(value = EnumType.ORDINAL)
    private UserStatus userStatus;
}

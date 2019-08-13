package greencity.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSecurity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne private User user;

    @OneToOne private UserGoogleSecurityDetail userGoogleSecurityDetail;

    @OneToOne private UserOwnSecurityDetail userOwnSecurityDetail;
}

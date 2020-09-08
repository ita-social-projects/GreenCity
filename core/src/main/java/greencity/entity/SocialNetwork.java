package greencity.entity;

import javax.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "social_networks")
public class SocialNetwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 1, max = 500)
    @Column(name = "social_network_url", length = 500)
    String url;

    @ManyToOne
    User user;

    @ManyToOne
    SocialNetworkImage socialNetworkImage;
}

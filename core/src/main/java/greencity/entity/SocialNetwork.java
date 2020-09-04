package greencity.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.*;

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

    @Size(max = 500)
    @Column(name = "social_network_url")
    String url;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @OneToOne(fetch = FetchType.LAZY)
    SocialNetworkImage socialNetworkImage;
}

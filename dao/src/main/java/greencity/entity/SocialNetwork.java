package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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

    @Size(min = 1, max = 500)
    @Column(name = "social_network_url", length = 500)
    String url;

    @ManyToOne
    User user;

    @ManyToOne
    SocialNetworkImage socialNetworkImage;
}

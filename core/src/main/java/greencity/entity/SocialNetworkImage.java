package greencity.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "social_networks_images")
public class SocialNetworkImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_path")
    String imagePath;

    @Column(name = "host_path")
    String hostPath;
}

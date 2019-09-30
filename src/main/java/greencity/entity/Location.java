package greencity.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Double lat;

    @Column(nullable = false, unique = true)
    private Double lng;

    @Column(name = "address", nullable = false)
    private String address;

    @OneToOne(mappedBy = "location")
    private Place place;
}

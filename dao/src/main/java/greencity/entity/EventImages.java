package greencity.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "events_images")
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NonNull
    private String link;

    @ManyToOne
    private Event event;
}

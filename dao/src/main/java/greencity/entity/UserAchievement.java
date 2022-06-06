package greencity.entity;

import javax.persistence.*;

import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "user_achievements")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@Setter

public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Achievement achievement;

    @Column(nullable = false)
    private ZonedDateTime achievedTimestamp;

    @Column(nullable = false)
    private boolean notified;
}

package greencity.entity;

import greencity.enums.ActionContextType;
import greencity.enums.UserActionType;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_actions")
@Builder
@EqualsAndHashCode
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column
    private ZonedDateTime timestamp;

    @Column
    @Enumerated(EnumType.STRING)
    private UserActionType actionType;

    @Column
    @Enumerated(EnumType.STRING)
    private ActionContextType contextType;

    @Column
    private Long contextId;
}

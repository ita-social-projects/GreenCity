package greencity.entity;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "direct_rooms")
public class DirectRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    Participant firstParticipant;

    @ManyToOne
    Participant secondParticipant;

    String lastMessageContent;
    ZonedDateTime lastMessageDate;

    @OneToMany(mappedBy = "directRoom")
    private List<DirectMessage> messages;
}

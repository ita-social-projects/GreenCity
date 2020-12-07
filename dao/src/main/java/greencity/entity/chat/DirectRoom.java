package greencity.entity.chat;

import greencity.entity.User;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "direct_rooms")
public class DirectRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    User firstParticipant;

    @ManyToOne
    User secondParticipant;

    String lastMessageContent;
    ZonedDateTime lastMessageDate;

    @OneToMany(mappedBy = "directRoom")
    private List<DirectMessage> messages;
}

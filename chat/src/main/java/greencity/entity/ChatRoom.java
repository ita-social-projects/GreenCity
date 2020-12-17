package greencity.entity;

import greencity.enums.ChatType;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"messages"})
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<ChatMessage> messages = new LinkedList<>();

    @Enumerated(value = EnumType.STRING)
    private ChatType type;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "chat_rooms_participants",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "participant_id"))
    private Set<Participant> participants;
}

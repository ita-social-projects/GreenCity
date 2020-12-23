package greencity.repository;

import greencity.entity.ChatMessage;
import greencity.entity.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long>,
    JpaSpecificationExecutor<ChatMessage> {
    /**
     * Method to find all {@link ChatMessage}'s by {@link ChatRoom} id.
     *
     * @param chatRoom {@link ChatRoom} instance.
     * @return list of {@link ChatMessage} instances.
     */
    List<ChatMessage> findAllByRoom(ChatRoom chatRoom);
}

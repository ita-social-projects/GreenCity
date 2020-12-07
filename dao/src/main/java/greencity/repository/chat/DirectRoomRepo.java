package greencity.repository.chat;

import greencity.entity.chat.DirectRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectRoomRepo extends JpaRepository<DirectRoom, Long> {
}

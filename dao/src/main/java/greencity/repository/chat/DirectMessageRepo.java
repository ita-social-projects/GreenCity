package greencity.repository.chat;

import greencity.entity.chat.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectMessageRepo extends JpaRepository<DirectMessage, Long> {
}

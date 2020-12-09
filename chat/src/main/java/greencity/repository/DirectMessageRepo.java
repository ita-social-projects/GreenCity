package greencity.repository;

import greencity.entity.DirectMessage;
import greencity.entity.DirectRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectMessageRepo extends JpaRepository<DirectMessage, Long>,
    JpaSpecificationExecutor<DirectMessage> {
    /**
     * Method to find all {@link DirectMessage}'s by {@link DirectRoom} id.
     *
     * @param directRoomId {@link DirectRoom} id.
     * @return list of {@link DirectRoom} instances.
     */
    List<DirectMessage> findAllByDirectRoomId(Long directRoomId);
}

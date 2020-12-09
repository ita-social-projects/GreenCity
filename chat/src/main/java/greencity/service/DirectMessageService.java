package greencity.service;

import greencity.entity.DirectMessage;
import greencity.entity.DirectRoom;
import java.util.List;

public interface DirectMessageService {
    /**
     * Method to find all {@link DirectMessage}'s by {@link DirectRoom} id.
     *
     * @param directRoomId {@link DirectMessage} id.
     * @return list of {@link DirectMessage} instances.
     */
    List<DirectMessage> findAllMessagesByDirectRoom(Long directRoomId);

    void processMessage(DirectMessage chatMessage);
}

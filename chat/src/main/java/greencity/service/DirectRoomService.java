package greencity.service;

import greencity.dto.UserVO;
import greencity.entity.DirectRoom;
import java.util.List;

public interface DirectRoomService {
    /**
     * Method to find {@link DirectRoom} by two participants {@link UserVO} id's
     * (order not matters).
     *
     * @param firstParticipantId  first participant {@link UserVO} id.
     * @param secondParticipantId second participant {@link UserVO} id.
     * @return {@link DirectRoom} instance.
     */
    DirectRoom findDirectRoomByParticipants(Long firstParticipantId,
                                              Long secondParticipantId);

    /**
     * Method to find all {@link DirectRoom}'s by participant {@link UserVO} id
     * (order not matters).
     *
     * @param participantId participant {@link UserVO} id.
     * @return list of {@link DirectRoom} instances.
     */
    List<DirectRoom> findAllDirectRoomsByParticipant(Long participantId);
}

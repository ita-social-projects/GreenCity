package greencity.service;

import greencity.entity.DirectRoom;
import greencity.entity.Participant;
import java.util.List;

public interface DirectRoomService {
    /**
     * Method to find {@link DirectRoom} by two {@link Participant} id's
     * (order not matters).
     *
     * @param firstParticipantId  first {@link Participant} id.
     * @param secondParticipantId second {@link Participant} id.
     * @return {@link DirectRoom} instance.
     */
    DirectRoom findDirectRoomByParticipants(Long firstParticipantId,
                                            Long secondParticipantId);

    /**
     * Method to find all {@link DirectRoom}'s by {@link Participant} id
     * (order not matters).
     *
     * @param participantId {@link Participant} id.
     * @return list of {@link DirectRoom} instances.
     */
    List<DirectRoom> findAllDirectRoomsByParticipant(Long participantId);
}

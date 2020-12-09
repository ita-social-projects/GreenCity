package greencity.service.impl;

import greencity.entity.DirectRoom;
import greencity.repository.DirectRoomRepo;
import greencity.service.DirectRoomService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link DirectRoomService}.
 */
@Service
@AllArgsConstructor
public class DirectRoomServiceImpl implements DirectRoomService {
    private final DirectRoomRepo directRoomRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectRoom findDirectRoomByParticipants(Long firstParticipantId, Long secondParticipantId) {
        return directRoomRepo.findByParticipants(firstParticipantId, secondParticipantId)
            .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DirectRoom> findAllDirectRoomsByParticipant(Long participantId) {
        return null;
    }
}

package greencity.service.impl;

import greencity.dto.DirectRoomDto;
import greencity.repository.DirectRoomRepo;
import greencity.service.DirectRoomService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link DirectRoomService}.
 */
@Service
@AllArgsConstructor
public class DirectRoomServiceImpl implements DirectRoomService {
    private final DirectRoomRepo directRoomRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectRoomDto findDirectRoomByParticipants(Long firstParticipantId, Long secondParticipantId) {
        return /*directRoomRepo.findByParticipants(firstParticipantId, secondParticipantId)
            .orElseThrow()*/ null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DirectRoomDto> findAllDirectRoomsByParticipant(Long participantId) {
        return modelMapper
            .map(directRoomRepo.findAllByParticipant(participantId), new TypeToken<List<DirectRoomDto>>() {
            }.getType());
    }
}

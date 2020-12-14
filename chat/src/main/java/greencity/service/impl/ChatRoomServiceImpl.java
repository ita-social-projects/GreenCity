package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.ChatRoomDto;
import greencity.entity.ChatRoom;
import greencity.entity.Participant;
import greencity.enums.ChatType;
import greencity.exception.exceptions.ChatRoomNotFoundException;
import greencity.repository.ChatRoomRepo;
import greencity.service.ChatRoomService;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ChatRoomService}.
 */
@Service
@AllArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepo chatRoomRepo;
    private final ModelMapper modelMapper;


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatRoomDto> findAllByParticipantId(Long participantId) {
        return modelMapper
            .map(chatRoomRepo.findAllByParticipantId(participantId), new TypeToken<List<ChatRoomDto>>() {
            }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatRoomDto> findAllRoomsByParticipantsAndStatus(Set<Participant> participants, ChatType chatType) {
        return modelMapper
            .map(chatRoomRepo.findByParticipantsAndStatus(participants, participants.size(), chatType),
                new TypeToken<List<ChatRoomDto>>() {
                }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatRoom findChatRoomById(Long id) {
        return chatRoomRepo.findById(id)
            .orElseThrow(() -> new ChatRoomNotFoundException(ErrorMessage.CHAT_ROOM_NOT_FOUND_BY_ID));
    }
}

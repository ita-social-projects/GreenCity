package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.ChatRoomDto;
import greencity.entity.ChatRoom;
import greencity.entity.Participant;
import greencity.enums.ChatType;
import greencity.exception.exceptions.ChatRoomNotFoundException;
import greencity.repository.ChatRoomRepo;
import greencity.repository.ParticipantRepo;
import greencity.service.ChatRoomService;
import greencity.service.ParticipantService;
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
    private final ParticipantService participantService;
    private final ModelMapper modelMapper;


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatRoomDto> findAllByParticipantId(Long participantId) {
        Participant participant = modelMapper.map(participantService.findById(participantId), Participant.class);
        return modelMapper
            .map(chatRoomRepo.findAllByParticipant(participant), new TypeToken<List<ChatRoomDto>>() {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatRoomDto findPrivateByParticipants(Long id, String name) {
        Participant first = modelMapper.map(participantService.findByEmail(name), Participant.class);
        Participant second = modelMapper.map(participantService.findById(id), Participant.class);
        Set<Participant> participants = Set.of(first, second);
        List<ChatRoom> chatRoom = chatRoomRepo.findByParticipantsAndStatus(
            participants, participants.size(), ChatType.PRIVATE);
        if (chatRoom.isEmpty()) {
            ChatRoom newChatRoom = ChatRoom.builder()
                .name("p2").participants(participants)
                .type(ChatType.PRIVATE)
                .build();
            return (modelMapper.map(chatRoomRepo.save(newChatRoom), ChatRoomDto.class));
        }
        return modelMapper.map(chatRoom.get(0), ChatRoomDto.class);
    }
}

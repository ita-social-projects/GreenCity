package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.ChatRoomDto;
import greencity.entity.ChatRoom;
import greencity.entity.Participant;
import greencity.enums.ChatType;
import greencity.exception.exceptions.ChatRoomNotFoundException;
import greencity.repository.ChatRoomRepo;
import greencity.service.ChatRoomService;
import greencity.service.ParticipantService;

import java.util.HashSet;
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
    public List<ChatRoomDto> findAllByParticipantName(String name) {
        Participant participant = participantService.findByEmail(name);
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
    public ChatRoomDto findChatRoomById(Long id) {
        ChatRoom chatRoom = chatRoomRepo.findById(id)
            .orElseThrow(() -> new ChatRoomNotFoundException(ErrorMessage.CHAT_ROOM_NOT_FOUND_BY_ID));
        return modelMapper.map(chatRoom, ChatRoomDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatRoomDto findPrivateByParticipants(Long id, String name) {
        Set<Participant> participants = new HashSet<>();
        participants.add(participantService.findByEmail(name));
        participants.add(participantService.findById(id));
        List<ChatRoom> chatRoom = chatRoomRepo.findByParticipantsAndStatus(participants, participants.size(),
            ChatType.PRIVATE);
        return filterPrivateRoom(chatRoom, participants);
    }

    /**
     * {@inheritDoc}
     */
    private ChatRoomDto filterPrivateRoom(List<ChatRoom> chatRoom, Set<Participant> participants) {
        ChatRoom toReturn;
        if (chatRoom.isEmpty()) {
            toReturn = chatRoomRepo.save(
                ChatRoom.builder()
                    .name("chatName")
                    .participants(participants)
                    .type(ChatType.PRIVATE)
                    .build());
        } else {
            toReturn = chatRoom.get(0);
        }
        return modelMapper.map(toReturn, ChatRoomDto.class);
    }
}

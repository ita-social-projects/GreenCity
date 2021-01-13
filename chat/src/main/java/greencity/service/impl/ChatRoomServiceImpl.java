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

import java.util.*;
import java.util.stream.Collectors;

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
    public List<ChatRoomDto> findAllVisibleRooms(String name) {
        Participant participant = participantService.findByEmail(name);
        List<ChatRoom> list = chatRoomRepo.findAllByParticipant(participant);
        return modelMapper
                .map(list.stream().filter(chatRoom -> chatRoom.getMessages().size() > 0 && chatRoom.getType().equals(ChatType.PRIVATE) || chatRoom.getType().equals(ChatType.GROUP)).collect(Collectors.toList()), new TypeToken<List<ChatRoomDto>>() {
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
        Set<Participant> participants = new LinkedHashSet<>();
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
                            .name(participants.stream().map(Participant::getName).collect(Collectors.joining("+")))
                            .messages(new ArrayList<>())
                            .participants(participants)
                            .type(ChatType.PRIVATE)
                            .build());
        } else {
            toReturn = chatRoom.get(0);
        }

        return modelMapper.map(toReturn, ChatRoomDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatRoomDto> findGroupByParticipants(List <Long> ids, String name, String chatName) {
        Set<Participant> participants = new HashSet<>();
        participants.add(participantService.findByEmail(name));
        ids.forEach(id -> participants.add(participantService.findById(id)));
        List<ChatRoom> chatRoom = chatRoomRepo.findByParticipantsAndStatus(participants, participants.size(),
                ChatType.GROUP);
        return filterGroupRoom(chatRoom, participants, chatName);
    }

    /**
     * {@inheritDoc}
     */
    private List<ChatRoomDto> filterGroupRoom(List<ChatRoom> chatRoom, Set<Participant> participants, String chatName) {
        List<ChatRoom> toReturn = new ArrayList<>();
        if (chatRoom.isEmpty()) {
            toReturn.add(chatRoomRepo.save(
                    ChatRoom.builder()
                            .name(chatName)
                            .participants(participants)
                            .type(ChatType.GROUP)
                            .build()));
        } else {
            toReturn = chatRoom;
        }
        return toReturn.stream().map(room -> modelMapper.map(room, ChatRoomDto.class)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatRoomDto> findGroupChatRooms(Participant participant, ChatType chatType) {
        return chatRoomRepo.findGroupChats(participant, chatType).stream()
                .map(room -> modelMapper.map(room, ChatRoomDto.class))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatRoomDto> findAllChatRoomsByQuery(String query, Participant participant) {
        return modelMapper.map(
                chatRoomRepo.findAllChatRoomsByQuery(query, participant),
                new TypeToken<List<ChatRoomDto>>() {
                }.getType());
    }
}

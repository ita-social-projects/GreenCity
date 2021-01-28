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
        List<ChatRoom> rooms = chatRoomRepo.findAllByParticipant(participant);
        return modelMapper
            .map(
                rooms.stream()
                    .filter(chatRoom -> !chatRoom.getMessages().isEmpty() && chatRoom.getType().equals(ChatType.PRIVATE)
                        || chatRoom.getType().equals(ChatType.GROUP))
                    .collect(Collectors.toList()),
                new TypeToken<List<ChatRoomDto>>() {
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
        Participant owner = participantService.findByEmail(name);
        participants.add(owner);
        participants.add(participantService.findById(id));
        List<ChatRoom> chatRoom = chatRoomRepo.findByParticipantsAndStatus(participants, participants.size(),
            ChatType.PRIVATE);
        return filterPrivateRoom(chatRoom, participants, owner);
    }

    /**
     * {@inheritDoc}
     */
    private ChatRoomDto filterPrivateRoom(List<ChatRoom> chatRoom, Set<Participant> participants, Participant owner) {
        ChatRoom toReturn;
        if (chatRoom.isEmpty()) {
            toReturn = chatRoomRepo.save(
                ChatRoom.builder()
                    .name(participants.stream().map(Participant::getName).collect(Collectors.joining(":")))
                    .owner(owner)
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
    public List<ChatRoomDto> findGroupByParticipants(List<Long> ids, String name, String chatName) {
        Set<Participant> participants = new HashSet<>();
        Participant owner = participantService.findByEmail(name);
        participants.add(owner);
        ids.forEach(id -> participants.add(participantService.findById(id)));
        List<ChatRoom> chatRoom = chatRoomRepo.findByParticipantsAndStatus(participants, participants.size(),
            ChatType.GROUP);
        return filterGroupRoom(chatRoom, participants, chatName, owner);
    }

    /**
     * {@inheritDoc}
     */
    private List<ChatRoomDto> filterGroupRoom(List<ChatRoom> chatRoom, Set<Participant> participants,
        String chatName, Participant owner) {
        List<ChatRoom> toReturn = new ArrayList<>();
        if (chatRoom.isEmpty()) {
            toReturn.add(chatRoomRepo.save(
                ChatRoom.builder()
                    .name(chatName)
                    .participants(participants)
                    .owner(owner)
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
        List<ChatRoom> rooms = chatRoomRepo.findAllChatRoomsByQuery(query, participant);
        return modelMapper
            .map(
                rooms.stream()
                    .filter(chatRoom -> !chatRoom.getMessages().isEmpty()
                        && chatRoom.getType().equals(ChatType.PRIVATE)
                        || chatRoom.getType().equals(ChatType.GROUP))
                    .collect(Collectors.toList()),
                new TypeToken<List<ChatRoomDto>>() {
                }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatRoomDto deleteChatRoom(Long roomId, String email) {
        Participant participant = participantService.findByEmail(email);
        List<ChatRoom> list = chatRoomRepo.findAllByParticipant(participant);
        ChatRoom deleteRoom = list.stream().filter(chatRoom -> chatRoom.getId().equals(roomId)).findAny().orElseThrow();
        ChatRoomDto chatRoomDto = modelMapper.map(deleteRoom, ChatRoomDto.class);
        chatRoomRepo.delete(deleteRoom);
        return chatRoomDto;
    }

    @Override
    public ChatRoomDto leaveChatRoom(ChatRoomDto chatRoomDto, String email, Long ownerId) {
        ChatRoom chatRoom = modelMapper.map(chatRoomDto, ChatRoom.class);
        chatRoom.setOwner(participantService.findById(ownerId));
        chatRoom.setType(ChatType.GROUP);
        chatRoom.getParticipants().removeIf(participant -> participant.getEmail().equals(email));
        chatRoomRepo.save(chatRoom);
        return null;
    }

    @Override
    public ChatRoomDto manageParticipantsAndNameChatRoom(ChatRoomDto chatRoomDto, String email) {
        ChatRoom chatRoom = modelMapper.map(chatRoomDto, ChatRoom.class);
        chatRoom.setOwner(participantService.findByEmail(email));
        chatRoom.setType(ChatType.GROUP);
        chatRoomRepo.save(chatRoom);
        return chatRoomDto;
    }
}

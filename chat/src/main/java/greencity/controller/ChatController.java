package greencity.controller;

import greencity.dto.ChatMessageDto;
import greencity.dto.ChatRoomDto;
import greencity.dto.ParticipantDto;
import greencity.entity.ChatRoom;
import greencity.enums.ChatType;
import greencity.repository.ChatRoomRepo;
import greencity.service.ChatMessageService;
import greencity.service.ChatRoomService;
import greencity.service.ParticipantService;
import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final ParticipantService participantService;
    private final ChatMessageService chatMessageService;
    private final ChatRoomRepo chatRoomRepo;

    /**
     * {@inheritDoc}
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> findAllRooms(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatRoomService.findAllByParticipantName(principal.getName()));
    }

    @GetMapping("/rooms/visible")
    public ResponseEntity<List<ChatRoomDto>> findAllVisibleRooms(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatRoomService.findAllVisibleRooms(principal.getName()));
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping("/messages/{room_id}")
    public ResponseEntity<List<ChatMessageDto>> findAllMessages(@PathVariable("room_id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatMessageService.findAllMessagesByChatRoomId(id));
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<ChatRoomDto> findPrivateRoomWithUser(@PathVariable Long id, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatRoomService.findPrivateByParticipants(id, principal.getName()));
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping("/room/{room_id}")
    public ResponseEntity<ChatRoomDto> findRoomById(@PathVariable("room_id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatRoomService.findChatRoomById(id));
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping("/user")
    public ResponseEntity<ParticipantDto> getCurrentUser(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(participantService.getCurrentParticipantByEmail(principal.getName()));
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping(value = {"/users", "/users/{query}"})
    public ResponseEntity<List<ParticipantDto>> getAllParticipantsBy(
            @PathVariable(required = false, value = "query") String query, Principal principal) {
        if (StringUtils.isEmpty(query)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(participantService.findAllExceptCurrentUser(principal.getName()));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(participantService.findAllParticipantsByQuery(query, principal.getName()));
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping(value = {"/rooms", "/rooms/{query}"})
    public ResponseEntity<List<ChatRoomDto>> getAllChatRoomsBy(
            @PathVariable(required = false, value = "query") String query, Principal principal) {
        if (StringUtils.isEmpty(query)) {
            return this.findAllVisibleRooms(principal);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatRoomService.findAllChatRoomsByQuery(query, participantService.findByEmail(principal.getName())));
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping("/last/message")
    public ResponseEntity<Long> getLastId() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatMessageService.findTopByOrderByIdDesc().getId());
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping("/users/{ids}/room/{room_name}")
    public ResponseEntity<List<ChatRoomDto>> getGroupChatRoomsWithUsers(@PathVariable("ids") List<Long> ids,
                                                                        @PathVariable("room_name") String chatName,
                                                                        Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatRoomService.findGroupByParticipants(ids, principal.getName(), chatName));
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping("/groups")
    public ResponseEntity<List<ChatRoomDto>> getGroupChats(Principal principal) {
        List<ChatRoom> list = chatRoomRepo.findGroupChats(participantService.findByEmail(principal.getName()), ChatType.GROUP);
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatRoomService.findGroupChatRooms(participantService.findByEmail(principal.getName()),ChatType.GROUP));
    }

    /**
     * {@inheritDoc}
     */
    @MessageMapping("/chat")
    public void processMessage(ChatMessageDto chatMessageDto) {
        chatMessageService.processMessage(chatMessageDto);
    }

    /**
     * {@inheritDoc}
     */
    @MessageMapping("/chat/delete")
    public void deleteMessage(ChatMessageDto chatMessageDto) {
        chatMessageService.deleteMessage(chatMessageDto);
    }

    /**
     * {@inheritDoc}
     */
    @MessageMapping("/chat/update")
    public void updateMessage(ChatMessageDto chatMessageDto) {
        chatMessageService.updateMessage(chatMessageDto);
    }
}

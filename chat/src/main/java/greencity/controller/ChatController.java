package greencity.controller;

import greencity.dto.ChatMessageDto;
import greencity.dto.ChatRoomDto;
import greencity.dto.ParticipantDto;
import greencity.service.ChatMessageService;
import greencity.service.ChatRoomService;
import greencity.service.ParticipantService;
import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final ParticipantService participantService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> findAllRooms(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(chatRoomService.findAllByParticipantName(principal.getName()));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ChatRoomDto> findPrivateRoomWithUser(@PathVariable Long id, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(chatRoomService.findPrivateByParticipants(id, principal.getName()));
    }

    @GetMapping("/user")
    public ResponseEntity<ParticipantDto> getCurrentUser(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(participantService.getCurrentParticipantByEmail(principal.getName()));
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDto chatMessageDto) {
        chatMessageService.processMessage(chatMessageDto);
    }
}

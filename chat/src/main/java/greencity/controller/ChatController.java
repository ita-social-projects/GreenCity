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

    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> findAllRooms(Principal principal) {
        ParticipantDto participant = participantService.findByEmail(principal.getName());
        return ResponseEntity.status(HttpStatus.OK)
            .body(chatRoomService.findAllByParticipantId(participant.getId()));
    }

    @GetMapping("/{id}")
    public String findMessageRoom(@PathVariable Long id, Principal principal) {
        ParticipantDto partSecond = participantService.findById(id);
        ParticipantDto partFirst = participantService.findByEmail(principal.getName());
        //DirectRoom directRoom = directRoomService.findDirectRoomByParticipants(partFirst.getId(), partSecond.getId());
        //System.out.println(directRoom);
        System.out.println(partFirst);
        System.out.println(partSecond);
        return "/chat";
    }

    @GetMapping("/participant/{id}")
    public ResponseEntity<ParticipantDto> findParticipant(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(participantService.findById(id));
    }

    @GetMapping("/participant")
    public ResponseEntity<ParticipantDto> getCurrentParticipant(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(participantService.getCurrentParticipantByEmail(principal.getName()));
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDto chatMessageDto) {
        chatMessageService.processMessage(chatMessageDto);
    }
}

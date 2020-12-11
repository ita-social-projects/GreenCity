package greencity.controller;

import greencity.dto.DirectRoomDto;
import greencity.dto.ParticipantDto;
import greencity.entity.DirectMessage;
import greencity.entity.DirectRoom;
import greencity.service.DirectMessageService;
import greencity.service.DirectRoomService;
import greencity.service.ParticipantService;
import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final DirectMessageService directMessageService;
    private final DirectRoomService directRoomService;
    private final ParticipantService participantService;

    @GetMapping
    public ResponseEntity<List<DirectRoomDto>> findAllRooms(Principal principal, Model model) {
        ParticipantDto partFirst = participantService.findByEmail(principal.getName());
        return ResponseEntity.status(HttpStatus.OK)
            .body(directRoomService.findAllDirectRoomsByParticipant(partFirst.getId()));
    }

    @GetMapping("/{id}")
    public String findMessageRoom(@PathVariable Long id, Model model, Principal principal) {
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

    @MessageMapping("/direct")
    public DirectMessage processMessage(@Payload DirectMessage chatMessage) {
        return directMessageService.processMessage(chatMessage);
    }
}

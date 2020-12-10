package greencity.controller;

import greencity.entity.DirectMessage;
import greencity.entity.DirectRoom;
import greencity.entity.Participant;
import greencity.service.DirectMessageService;
import greencity.service.DirectRoomService;
import greencity.service.ParticipantService;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final DirectMessageService directMessageService;
    private final DirectRoomService directRoomService;
    private final ParticipantService participantService;

    @GetMapping
    public String findAllRooms(Principal principal, Model model) {
        Participant partFirst = participantService.findByEmail(principal.getName());
        model.addAttribute("rooms", directRoomService.findAllDirectRoomsByParticipant(partFirst.getId()));
        return "/chat";
    }

    @GetMapping("/{id}")
    public String findMessageRoom(@PathVariable Long id, Model model, Principal principal) {
        Participant partSecond = participantService.findById(id);
        Participant partFirst = participantService.findByEmail(principal.getName());
        DirectRoom directRoom = directRoomService.findDirectRoomByParticipants(partFirst.getId(), partSecond.getId());
        System.out.println(directRoom);
        System.out.println(partFirst);
        System.out.println(partSecond);
        return "/chat";
    }

    @MessageMapping("/direct")
    public DirectMessage processMessage(@Payload DirectMessage chatMessage) {
        return directMessageService.processMessage(chatMessage);
    }
}

package greencity.controller;

import greencity.entity.DirectMessage;
import greencity.service.DirectMessageService;
import greencity.service.DirectRoomService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final DirectMessageService directMessageService;
    private final DirectRoomService directRoomService;

    @GetMapping
    public String getAllMessageRooms(Model model) {
        model.addAttribute("rooms", directRoomService.findAllDirectRoomsByParticipant(1L));
        return "/chat";
    }

    @MessageMapping("/direct")
    public void processMessage(@Payload DirectMessage chatMessage) {
        directMessageService.processMessage(chatMessage);
    }
}

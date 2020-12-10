package greencity.service.impl;

import greencity.entity.DirectMessage;
import greencity.repository.DirectMessageRepo;
import greencity.repository.DirectRoomRepo;
import greencity.service.DirectMessageService;
import greencity.service.DirectRoomService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link DirectRoomService}.
 */
@Service
@AllArgsConstructor
public class DirectMessageServiceImpl implements DirectMessageService {
    private final DirectMessageRepo directMessageRepo;
    private final DirectRoomRepo directRoomRepo;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DirectMessage> findAllMessagesByDirectRoom(Long directRoomId) {
        return null;
    }


    @Override
    public DirectMessage processMessage(DirectMessage directMessage) {
      /*  var chatId = directRoomRepo.findByParticipants(chatMessage.ge)
            .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);
        chatMessage.setChatId(chatId.get());*/
       // DirectMessage directMessage = modelMapper.map(directMessageVO, DirectMessage.class);
        DirectMessage saved = directMessageRepo.save(directMessage);
        return saved;
        /*messagingTemplate.convertAndSendToUser(String.valueOf(saved.getSender().getId()), "/queue/messages",
            new Object());*/
    }
}

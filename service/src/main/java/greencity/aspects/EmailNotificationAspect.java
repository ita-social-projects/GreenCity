package greencity.aspects;

import greencity.annotations.NotificationType;
import greencity.client.RestClient;
import greencity.dto.EmailSendable;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.event.EventDto;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.TypeOfEmailNotification;
import greencity.exception.exceptions.BadRequestException;
import greencity.message.AbstractEmailMessage;
import greencity.message.NewsCommentMessage;
import greencity.message.SendEventCreationNotification;
import greencity.repository.EcoNewsCommentRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class EmailNotificationAspect {

    private final RestClient restClient;
    private final EcoNewsCommentRepo ecoNewsCommentRepo;
    private final UserRepo userRepo;

    @AfterReturning(
            value = "@annotation(typeOfNotification)",
            returning = "result"
    )
    public void defineEmailNotification(NotificationType typeOfNotification, EmailSendable result) {
        TypeOfEmailNotification notificationType = typeOfNotification.type();
        AbstractEmailMessage emailMessage;
        switch (notificationType) {
            case EVENT_CREATED:
                EventDto castedEventDto = (EventDto) result;
                emailMessage = SendEventCreationNotification.builder()
                        .email(castedEventDto.getOrganizer().getEmail())
                        .name(castedEventDto.getOrganizer().getName())
                        .subject("Event created")
                        .message("You successfully crated an event: " + castedEventDto.getTitle())
                        .build();
                restClient.sendEmailNotification(emailMessage);
                break;
            case NEWS_COMMENTED:
                AddEcoNewsCommentDtoResponse castedEcoNewsCommentDto = (AddEcoNewsCommentDtoResponse) result;
                EcoNewsComment ecoNewsComment = ecoNewsCommentRepo.findById(castedEcoNewsCommentDto.getId())
                        .orElseThrow(()->new BadRequestException("the comment was not found"));
                Long userId = ecoNewsComment.getEcoNews().getAuthor().getId();
                User user = userRepo.findById(userId).orElseThrow(()->new BadRequestException("the user was not found"));
                emailMessage = NewsCommentMessage.builder()
                        .email(user.getEmail())
                        .name(ecoNewsComment.getUser().getName())
                        .subject("You have received a comment on your eco news")
                        .message("You received a comment: " + castedEcoNewsCommentDto.getText())
                        .build();
                restClient.sendEmailNotification(emailMessage);
                break;
            default:
                System.out.println("Unknown user action");
        }
    }

}

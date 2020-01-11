package greencity.event.messages;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class, that contains needed data for news sending by mail.
 */
@AllArgsConstructor
@Data
public class SendNewsByEmailMessage {
    private List<NewsSubscriberResponseDto> subscribers;
    private AddEcoNewsDtoResponse newsDto;
}

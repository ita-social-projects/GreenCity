package greencity.message;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message, that is used for sending emails about adding new eco news.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEcoNewsMessage implements Serializable {
    private List<NewsSubscriberResponseDto> subscribers;
    private AddEcoNewsDtoResponse addEcoNewsDtoResponse;
}

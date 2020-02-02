package greencity.message;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.EcoNews;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message, that is used for sending emails about adding new {@link EcoNews}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEcoNewsMessage implements Serializable {
    private List<NewsSubscriberResponseDto> subscribers;
    private AddEcoNewsDtoResponse addEcoNewsDtoResponse;
}

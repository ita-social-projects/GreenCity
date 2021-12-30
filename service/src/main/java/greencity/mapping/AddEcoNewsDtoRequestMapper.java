package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.entity.EcoNews;
import java.time.ZonedDateTime;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link AddEcoNewsDtoRequest}
 * into {@link EcoNews}.
 */
@Component
public class AddEcoNewsDtoRequestMapper extends AbstractConverter<AddEcoNewsDtoRequest, EcoNews> {
    /**
     * Method for converting {@link AddEcoNewsDtoRequest} into {@link EcoNews}.
     *
     * @param addEcoNewsDtoRequest object to convert.
     * @return converted object.
     */
    @Override
    protected EcoNews convert(AddEcoNewsDtoRequest addEcoNewsDtoRequest) {
        return EcoNews.builder()
            .source(addEcoNewsDtoRequest.getSource())
            .title(addEcoNewsDtoRequest.getTitle())
            .text(addEcoNewsDtoRequest.getText())
            .shortInfo(addEcoNewsDtoRequest.getShortInfo())
            .creationDate(ZonedDateTime.now())
            .build();
    }
}

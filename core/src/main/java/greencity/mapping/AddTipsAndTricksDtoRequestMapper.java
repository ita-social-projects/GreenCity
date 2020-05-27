package greencity.mapping;

import greencity.dto.tipsandtricks.AddTipsAndTricksDtoRequest;
import greencity.entity.TipsAndTricks;
import java.time.ZonedDateTime;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link AddTipsAndTricksDtoRequest} into
 * {@link TipsAndTricks}.
 */
@Component
public class AddTipsAndTricksDtoRequestMapper extends AbstractConverter<AddTipsAndTricksDtoRequest, TipsAndTricks> {
    /**
     * Method for converting {@link AddTipsAndTricksDtoRequest} into {@link TipsAndTricks}.
     *
     * @param addTipsAndTricksDtoRequest object to convert.
     * @return converted object.
     */
    @Override
    protected TipsAndTricks convert(AddTipsAndTricksDtoRequest addTipsAndTricksDtoRequest) {
        return TipsAndTricks.builder()
            .source(addTipsAndTricksDtoRequest.getSource())
            .title(addTipsAndTricksDtoRequest.getTitle())
            .text(addTipsAndTricksDtoRequest.getText())
            .creationDate(ZonedDateTime.now())
            .build();
    }
}

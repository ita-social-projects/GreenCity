package greencity.mapping;

import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.entity.TipsAndTricks;
import java.time.ZonedDateTime;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link TipsAndTricksDtoRequest} into
 * {@link TipsAndTricks}.
 */
@Component
public class TipsAndTricksDtoRequestMapper extends AbstractConverter<TipsAndTricksDtoRequest, TipsAndTricks> {
    /**
     * Method for converting {@link TipsAndTricksDtoRequest} into {@link TipsAndTricks}.
     *
     * @param tipsAndTricksDtoRequest object to convert.
     * @return converted object.
     */
    @Override
    protected TipsAndTricks convert(TipsAndTricksDtoRequest tipsAndTricksDtoRequest) {
        return TipsAndTricks.builder()
            .source(tipsAndTricksDtoRequest.getSource())
            .title(tipsAndTricksDtoRequest.getTitle())
            .text(tipsAndTricksDtoRequest.getText())
            .creationDate(ZonedDateTime.now())
            .build();
    }
}

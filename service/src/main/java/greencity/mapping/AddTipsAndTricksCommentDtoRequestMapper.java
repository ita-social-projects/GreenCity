package greencity.mapping;

import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.entity.TipsAndTricksComment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddTipsAndTricksCommentDtoRequestMapper
    extends AbstractConverter<AddTipsAndTricksCommentDtoRequest, TipsAndTricksComment> {
    @Override
    protected TipsAndTricksComment convert(AddTipsAndTricksCommentDtoRequest addTipsAndTricksCommentDtoRequest) {
        return TipsAndTricksComment.builder()
            .text(addTipsAndTricksCommentDtoRequest.getText())
            .build();
    }
}

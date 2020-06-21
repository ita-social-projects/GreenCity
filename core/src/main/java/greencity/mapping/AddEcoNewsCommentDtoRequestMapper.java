package greencity.mapping;

import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.entity.EcoNewsComment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddEcoNewsCommentDtoRequestMapper extends AbstractConverter<AddEcoNewsCommentDtoRequest, EcoNewsComment> {
    @Override
    protected EcoNewsComment convert(AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest) {
        return EcoNewsComment.builder()
            .text(addEcoNewsCommentDtoRequest.getText())
            .build();
    }
}

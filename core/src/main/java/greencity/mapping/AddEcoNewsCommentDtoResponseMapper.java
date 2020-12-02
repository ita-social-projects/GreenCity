package greencity.mapping;

import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddEcoNewsCommentDtoResponseMapper
    extends AbstractConverter<EcoNewsComment, AddEcoNewsCommentDtoResponse> {
    @Override
    protected AddEcoNewsCommentDtoResponse convert(EcoNewsComment ecoNewsComment) {
        User user = ecoNewsComment.getUser();

        return AddEcoNewsCommentDtoResponse.builder()
            .id(ecoNewsComment.getId())
            .modifiedDate(ecoNewsComment.getModifiedDate())
            .text(ecoNewsComment.getText())
            .author(EcoNewsCommentAuthorDto.builder()
                .id(user.getId())
                .name(user.getName())
                .userProfilePicturePath(user.getProfilePicturePath())
                .build())
            .build();
    }
}

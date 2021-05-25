package greencity.mapping;

import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentAuthorDto;
import greencity.entity.TipsAndTricksComment;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddTipsAndTricksCommentDtoResponseMapper
    extends AbstractConverter<TipsAndTricksComment, AddTipsAndTricksCommentDtoResponse> {
    @Override
    protected AddTipsAndTricksCommentDtoResponse convert(TipsAndTricksComment tipsAndTricksComment) {
        User user = tipsAndTricksComment.getUser();

        return AddTipsAndTricksCommentDtoResponse.builder()
            .id(tipsAndTricksComment.getId())
            .modifiedDate(tipsAndTricksComment.getModifiedDate())
            .text(tipsAndTricksComment.getText())
            .author(TipsAndTricksCommentAuthorDto.builder()
                .id(user.getId())
                .name(user.getName())
                .userProfilePicturePath(user.getProfilePicturePath())
                .build())
            .build();
    }
}
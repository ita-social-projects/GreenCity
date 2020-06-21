package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.entity.EcoNewsComment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EcoNewsCommentDtoMapper extends AbstractConverter<EcoNewsComment, EcoNewsCommentDto> {
    @Override
    protected EcoNewsCommentDto convert(EcoNewsComment ecoNewsComment) {
        return EcoNewsCommentDto.builder()
            .id(ecoNewsComment.getId())
            .text(ecoNewsComment.getText())
            .modifiedDate(ecoNewsComment.getModifiedDate())
            .author(EcoNewsCommentAuthorDto.builder()
                .id(ecoNewsComment.getUser().getId())
                .name(ecoNewsComment.getUser().getName())
                .userProfilePicturePath(ecoNewsComment.getUser().getProfilePicturePath())
                .build())
            .likes(ecoNewsComment.getUsersLiked().size())
            .currentUserLiked(ecoNewsComment.isCurrentUserLiked())
            .build();
    }
}

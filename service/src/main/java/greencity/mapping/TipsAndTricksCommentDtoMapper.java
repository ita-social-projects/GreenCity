package greencity.mapping;

import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentAuthorDto;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentDto;
import greencity.entity.TipsAndTricksComment;
import greencity.enums.CommentStatus;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class TipsAndTricksCommentDtoMapper extends AbstractConverter<TipsAndTricksComment, TipsAndTricksCommentDto> {
    @Override
    protected TipsAndTricksCommentDto convert(TipsAndTricksComment tipsAndTricksComment) {
        TipsAndTricksCommentDto dto = new TipsAndTricksCommentDto();
        dto.setId(tipsAndTricksComment.getId());
        dto.setModifiedDate(tipsAndTricksComment.getModifiedDate());
        if (tipsAndTricksComment.isDeleted()) {
            dto.setStatus(CommentStatus.DELETED);
            return dto;
        } else if (tipsAndTricksComment.getCreatedDate().isEqual(tipsAndTricksComment.getModifiedDate())) {
            dto.setStatus(CommentStatus.ORIGINAL);
        } else {
            dto.setStatus(CommentStatus.EDITED);
        }
        dto.setText(tipsAndTricksComment.getText());
        dto.setAuthor(TipsAndTricksCommentAuthorDto.builder()
            .id(tipsAndTricksComment.getUser().getId())
            .name(tipsAndTricksComment.getUser().getName())
            .userProfilePicturePath(tipsAndTricksComment.getUser().getProfilePicturePath())
            .build());
        dto.setLikes(tipsAndTricksComment.getUsersLiked().size());
        dto.setCurrentUserLiked(tipsAndTricksComment.isCurrentUserLiked());
        return dto;
    }
}

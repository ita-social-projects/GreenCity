package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.entity.EcoNewsComment;
import greencity.entity.enums.CommentStatus;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EcoNewsCommentDtoMapper extends AbstractConverter<EcoNewsComment, EcoNewsCommentDto> {
    @Override
    protected EcoNewsCommentDto convert(EcoNewsComment ecoNewsComment) {
        EcoNewsCommentDto dto = new EcoNewsCommentDto();
        dto.setId(ecoNewsComment.getId());
        dto.setModifiedDate(ecoNewsComment.getModifiedDate());
        if (ecoNewsComment.getDeleted()) {
            dto.setStatus(CommentStatus.Deleted);
            return dto;
        }

        if (ecoNewsComment.getCreatedDate().isEqual(ecoNewsComment.getModifiedDate())) {
            dto.setStatus(CommentStatus.Original);
        } else {
            dto.setStatus(CommentStatus.Edited);
        }
        dto.setText(ecoNewsComment.getText());
        dto.setAuthor(EcoNewsCommentAuthorDto.builder()
            .id(ecoNewsComment.getUser().getId())
            .name(ecoNewsComment.getUser().getName())
            .userProfilePicturePath(ecoNewsComment.getUser().getProfilePicturePath())
            .build());
        dto.setLikes(ecoNewsComment.getUsersLiked().size());
        dto.setCurrentUserLiked(ecoNewsComment.isCurrentUserLiked());
        return dto;
    }
}

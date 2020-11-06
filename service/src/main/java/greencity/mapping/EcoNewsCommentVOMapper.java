package greencity.mapping;

import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNewsComment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EcoNewsCommentVOMapper extends AbstractConverter<EcoNewsComment, EcoNewsCommentVO> {
    @Override
    protected EcoNewsCommentVO convert(EcoNewsComment ecoNewsComment) {
        return EcoNewsCommentVO.builder()
            .id(ecoNewsComment.getId())
            .user(UserVO.builder()
                .id(ecoNewsComment.getUser().getId())
                .role(ecoNewsComment.getUser().getRole())
                .name(ecoNewsComment.getUser().getName())
                .build())
            .modifiedDate(ecoNewsComment.getModifiedDate())
            .text(ecoNewsComment.getText())
            .deleted(ecoNewsComment.isDeleted())
            .currentUserLiked(ecoNewsComment.isCurrentUserLiked())
            .createdDate(ecoNewsComment.getCreatedDate())
            .usersLiked(ecoNewsComment.getUsersLiked().stream().map(user -> UserVO.builder()
                .id(user.getId())
                .build())
                .collect(Collectors.toSet()))
            .ecoNews(EcoNewsVO.builder()
                .id(ecoNewsComment.getEcoNews().getId())
                .build())
            .build();
    }
}

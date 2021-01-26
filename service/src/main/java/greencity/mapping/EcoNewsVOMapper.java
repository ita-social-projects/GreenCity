package greencity.mapping;

import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EcoNewsVOMapper extends AbstractConverter<EcoNews, EcoNewsVO> {
    @Override
    protected EcoNewsVO convert(EcoNews ecoNews) {
        return EcoNewsVO.builder()
            .id(ecoNews.getId())
            .author(UserVO.builder()
                .id(ecoNews.getAuthor().getId())
                .name(ecoNews.getAuthor().getName())
                .userStatus(ecoNews.getAuthor().getUserStatus())
                .role(ecoNews.getAuthor().getRole())
                .build())
            .creationDate(ecoNews.getCreationDate())
            .imagePath(ecoNews.getImagePath())
            .source(ecoNews.getSource())
            .text(ecoNews.getText())
            .title(ecoNews.getTitle())
            .tags(ecoNews.getTags().stream()
                .map(tag -> TagVO.builder()
                    .id(tag.getId())
                    .build())
                .collect(Collectors.toList()))
            .usersLikedNews(ecoNews.getUsersLikedNews().stream()
                .map(user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                    .collect(Collectors.toSet()))
            .ecoNewsComments(ecoNews.getEcoNewsComments().stream()
                .map(ecoNewsComment -> EcoNewsCommentVO.builder()
                    .id(ecoNewsComment.getId())
                    .createdDate(ecoNewsComment.getCreatedDate())
                    .currentUserLiked(ecoNewsComment.isCurrentUserLiked())
                    .deleted(ecoNewsComment.isDeleted())
                    .text(ecoNewsComment.getText())
                    .modifiedDate(ecoNewsComment.getModifiedDate())
                    .user(UserVO.builder()
                        .id(ecoNewsComment.getUser().getId())
                        .name(ecoNewsComment.getUser().getName())
                        .userStatus(ecoNewsComment.getUser().getUserStatus())
                        .role(ecoNewsComment.getUser().getRole())
                        .build())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}

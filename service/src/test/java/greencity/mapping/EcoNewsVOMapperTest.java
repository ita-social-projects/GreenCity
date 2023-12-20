package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import java.util.Collections;
import java.util.stream.Collectors;

import greencity.enums.CommentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcoNewsVOMapperTest {
    @InjectMocks
    EcoNewsVOMapper ecoNewsVOMapper;

    @Test
    void convert() {
        EcoNewsComment ecoNewsCommentExample = ModelUtils.getEcoNewsComment();
        EcoNews ecoNews = ModelUtils.getEcoNews();
        ecoNews.setEcoNewsComments(Collections.singletonList(ecoNewsCommentExample));

        EcoNewsVO expected = EcoNewsVO.builder()
            .id(ecoNews.getId())
            .author(UserVO.builder()
                .id(ecoNews.getAuthor().getId())
                .name(ecoNews.getAuthor().getName())
                .email(ecoNews.getAuthor().getEmail())
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
            .usersDislikedNews(ecoNews.getUsersDislikedNews().stream()
                .map(user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                .collect(Collectors.toSet()))
            .ecoNewsComments(ecoNews.getEcoNewsComments().stream()
                .map(ecoNewsComment -> EcoNewsCommentVO.builder()
                    .id(ecoNewsComment.getId())
                    .createdDate(ecoNewsComment.getCreatedDate())
                    .currentUserLiked(ecoNewsComment.isCurrentUserLiked())
                    .status(ecoNewsComment.getStatus())
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

        assertEquals(expected, ecoNewsVOMapper.convert(ecoNews));
    }
}

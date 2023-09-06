package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.enums.CommentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcoNewsCommentVOMapperTest {
    @InjectMocks
    EcoNewsCommentVOMapper mapper;

    @Test
    void convertWithoutParent() {
        EcoNewsCommentVO expected = ModelUtils.getEcoNewsCommentVOWithoutParentWithData();

        EcoNewsComment ecoNewsComment = EcoNewsComment.builder()
            .id(expected.getId())
            .user(User.builder()
                .id(expected.getUser().getId())
                .role(expected.getUser().getRole())
                .name(expected.getUser().getName())
                .build())
            .modifiedDate(expected.getModifiedDate())
            .parentComment(null)
            .text(expected.getText())
            .status(expected.getStatus())
            .currentUserLiked(expected.isCurrentUserLiked())
            .createdDate(expected.getCreatedDate())
            .usersLiked(expected.getUsersLiked().stream().map(user -> User.builder()
                .id(user.getId())
                .build()).collect(Collectors.toSet()))
            .ecoNews(EcoNews.builder()
                .id(expected.getEcoNews().getId())
                .build())
            .build();

        EcoNewsCommentVO actual = mapper.convert(ecoNewsComment);

        assertEquals(expected, actual);
    }

    @Test
    void convertWithParent() {
        EcoNewsCommentVO expected = ModelUtils.getEcoNewsCommentVOWithParentWithData();
        EcoNewsCommentVO expectedParent = expected.getParentComment();

        EcoNewsComment ecoNewsComment = EcoNewsComment.builder()
            .id(expected.getId())
            .user(User.builder()
                .id(expected.getUser().getId())
                .role(expected.getUser().getRole())
                .name(expected.getUser().getName())
                .build())
            .modifiedDate(expected.getModifiedDate())
            .text(expected.getText())
            .parentComment(EcoNewsComment.builder()
                .id(expectedParent.getId())
                .user(User.builder()
                    .id(expectedParent.getUser().getId())
                    .role(expectedParent.getUser().getRole())
                    .name(expectedParent.getUser().getName())
                    .build())
                .modifiedDate(expectedParent.getModifiedDate())
                .parentComment(null)
                .text(expectedParent.getText())
                .status(expectedParent.getStatus())
                .currentUserLiked(expectedParent.isCurrentUserLiked())
                .createdDate(expectedParent.getCreatedDate())
                .usersLiked(expectedParent.getUsersLiked().stream().map(user -> User.builder()
                    .id(user.getId())
                    .build()).collect(Collectors.toSet()))
                .ecoNews(EcoNews.builder()
                    .id(expectedParent.getEcoNews().getId())
                    .build())
                .build())
            .status(expected.getStatus())
            .currentUserLiked(expected.isCurrentUserLiked())
            .createdDate(expected.getCreatedDate())
            .usersLiked(expected.getUsersLiked().stream().map(user -> User.builder()
                .id(user.getId())
                .build()).collect(Collectors.toSet()))
            .ecoNews(EcoNews.builder()
                .id(expected.getEcoNews().getId())
                .build())
            .build();

        EcoNewsCommentVO actual = mapper.convert(ecoNewsComment);

        assertEquals(expected, actual);
    }
}
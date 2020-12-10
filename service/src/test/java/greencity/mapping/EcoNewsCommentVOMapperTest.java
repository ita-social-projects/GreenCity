package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class EcoNewsCommentVOMapperTest {
    @InjectMocks
    EcoNewsCommentVOMapper mapper;

    @Test
    void convert() {
        EcoNewsCommentVO expected = ModelUtils.getEcoNewsCommentVOWithData();

        EcoNewsComment ecoNewsComment = EcoNewsComment.builder()
            .id(expected.getId())
            .user(User.builder()
                .id(expected.getUser().getId())
                .role(expected.getUser().getRole())
                .name(expected.getUser().getName())
                .build())
            .modifiedDate(expected.getModifiedDate())
            .text(expected.getText())
            .deleted(expected.isDeleted())
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
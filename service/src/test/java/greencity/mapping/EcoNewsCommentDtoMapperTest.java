package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import greencity.ModelUtils;
import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.entity.EcoNewsComment;
import greencity.enums.CommentStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcoNewsCommentDtoMapperTest {

    @InjectMocks
    EcoNewsCommentDtoMapper ecoNewsCommentDtoMapper;
    private EcoNewsComment ecoNewsComment;
    private EcoNewsCommentDto expected;

    @BeforeEach
    void init() {
        ecoNewsComment = ModelUtils.getEcoNewsComment();
        ecoNewsComment.setModifiedDate(LocalDateTime.now().withNano(0));
        ecoNewsComment.setUsersLiked(Collections.singleton(ModelUtils.getUser()));
        ecoNewsComment.setCurrentUserLiked(true);
        expected = EcoNewsCommentDto.builder()
            .id(1L)
            .modifiedDate(ecoNewsComment.getModifiedDate())
            .build();
    }

    @Test
    void ecoNewsCommentIsDeleted() {
        expected.setStatus(CommentStatus.DELETED);
        ecoNewsComment.setStatus(CommentStatus.DELETED);
        assertEquals(expected, ecoNewsCommentDtoMapper.convert(ecoNewsComment));
    }

    @Test
    void ecoNewsCommentIsOriginal() {
        ecoNewsComment.setCreatedDate(LocalDateTime.now().withNano(0));
        expected.setStatus(CommentStatus.ORIGINAL);
        expected.setText(ecoNewsComment.getText());
        expected.setAuthor(EcoNewsCommentAuthorDto.builder()
            .id(ecoNewsComment.getUser().getId())
            .name(ecoNewsComment.getUser().getName())
            .userProfilePicturePath(ecoNewsComment.getUser().getProfilePicturePath())
            .build());
        expected.setLikes(ecoNewsComment.getUsersLiked().size());
        expected.setCurrentUserLiked(ecoNewsComment.isCurrentUserLiked());

        assertEquals(expected, ecoNewsCommentDtoMapper.convert(ecoNewsComment));
    }

    @Test
    void EcoNewsCommentIsEdited() {
        ecoNewsComment.setCreatedDate(LocalDateTime.now().minusDays(1).withNano(0));
        expected.setStatus(CommentStatus.EDITED);
        ecoNewsComment.setStatus(CommentStatus.EDITED);
        expected.setText(ecoNewsComment.getText());
        expected.setAuthor(EcoNewsCommentAuthorDto.builder()
            .id(ecoNewsComment.getUser().getId())
            .name(ecoNewsComment.getUser().getName())
            .userProfilePicturePath(ecoNewsComment.getUser().getProfilePicturePath())
            .build());
        expected.setLikes(ecoNewsComment.getUsersLiked().size());
        expected.setCurrentUserLiked(ecoNewsComment.isCurrentUserLiked());

        assertEquals(expected, ecoNewsCommentDtoMapper.convert(ecoNewsComment));
    }

}

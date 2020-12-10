package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import greencity.ModelUtils;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentAuthorDto;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentDto;
import greencity.entity.TipsAndTricksComment;
import greencity.enums.CommentStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class TipsAndTricksCommentDtoMapperTest {

    @InjectMocks
    TipsAndTricksCommentDtoMapper tipsAndTricksCommentDtoMapper;
    private TipsAndTricksComment tipsAndTricksComment;
    private TipsAndTricksCommentDto expected;

    @BeforeEach
    void init() {
        tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
        expected = TipsAndTricksCommentDto.builder()
            .id(1L)
            .modifiedDate(tipsAndTricksComment.getModifiedDate())
            .build();
    }

    @Test
    void tipsAndTricksCommentIsDeleted() {
        tipsAndTricksComment.setDeleted(true);
        expected.setStatus(CommentStatus.DELETED);
        assertEquals(expected, tipsAndTricksCommentDtoMapper.convert(tipsAndTricksComment));
    }

    @Test
    void tipsAndTricksCommentIsOriginal() {
        tipsAndTricksComment.setCreatedDate(LocalDateTime.now().withNano(0));
        expected.setStatus(CommentStatus.ORIGINAL);
        expected.setText(tipsAndTricksComment.getText());
        expected.setAuthor(TipsAndTricksCommentAuthorDto.builder()
            .id(tipsAndTricksComment.getUser().getId())
            .name(tipsAndTricksComment.getUser().getName())
            .userProfilePicturePath(tipsAndTricksComment.getUser().getProfilePicturePath())
            .build());
        expected.setLikes(tipsAndTricksComment.getUsersLiked().size());
        expected.setCurrentUserLiked(tipsAndTricksComment.isCurrentUserLiked());

        assertEquals(expected, tipsAndTricksCommentDtoMapper.convert(tipsAndTricksComment));
    }

    @Test
    void tipsAndTricksCommentIsEdited() {
        tipsAndTricksComment.setCreatedDate(LocalDateTime.now().minusDays(1).withNano(0));
        expected.setStatus(CommentStatus.EDITED);
        expected.setText(tipsAndTricksComment.getText());
        expected.setAuthor(TipsAndTricksCommentAuthorDto.builder()
            .id(tipsAndTricksComment.getUser().getId())
            .name(tipsAndTricksComment.getUser().getName())
            .userProfilePicturePath(tipsAndTricksComment.getUser().getProfilePicturePath())
            .build());
        expected.setLikes(tipsAndTricksComment.getUsersLiked().size());
        expected.setCurrentUserLiked(tipsAndTricksComment.isCurrentUserLiked());

        assertEquals(expected, tipsAndTricksCommentDtoMapper.convert(tipsAndTricksComment));
    }
}

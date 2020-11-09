package greencity.repository;

import greencity.entity.TipsAndTricksComment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql("classpath:sql/tipsandtricks_comment.sql")
class TipsAndTricksCommentRepoTest {

    @Autowired
    TipsAndTricksCommentRepo tipsAndTricksCommentRepo;

    @Test
    void findAllByParentCommentIsNullAndTipsAndTricksIdOrderByCreatedDateDescTest() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<TipsAndTricksComment> page = tipsAndTricksCommentRepo
            .findAllByParentCommentIsNullAndTipsAndTricksIdOrderByCreatedDateDesc(pageable, 6L);
        List<TipsAndTricksComment> commentList = page.get().collect(Collectors.toList());
        assertEquals(2, commentList.get(0).getId());
    }

    @Test
    void findAllByParentCommentIsNullAndTipsAndTricksIdNotFoundTest() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<TipsAndTricksComment> page = tipsAndTricksCommentRepo
            .findAllByParentCommentIsNullAndTipsAndTricksIdOrderByCreatedDateDesc(pageable, 10L);
        assertTrue(page.isEmpty());
    }

    @Test
    void findAllByParentCommentIdAndDeletedFalseOrderByCreatedDateAscTest() {
        List<TipsAndTricksComment> comments = tipsAndTricksCommentRepo
            .findAllByParentCommentIdAndDeletedFalseOrderByCreatedDateAsc(2L);
        assertEquals(2, comments.size());
        assertEquals(3, comments.get(0).getId());
    }

    @Test
    void findAllByParentCommentIdAndDeletedFalseNotFoundTest() {
        List<TipsAndTricksComment> comments = tipsAndTricksCommentRepo
            .findAllByParentCommentIdAndDeletedFalseOrderByCreatedDateAsc(5L);
        assertTrue(comments.isEmpty());
    }

    @Test
    void countAllByTipsAndTricksIdTest() {
        assertEquals(3, tipsAndTricksCommentRepo.countAllByTipsAndTricksId(6L));
    }

    @Test
    void countAllByTipsAndTricksIdNotFoundTest() {
        assertEquals(0, tipsAndTricksCommentRepo.countAllByTipsAndTricksId(10L));
    }

    @Test
    void countTipsAndTricksCommentByParentCommentIdAndDeletedFalseTest() {
        assertEquals(2, tipsAndTricksCommentRepo.countTipsAndTricksCommentByParentCommentIdAndDeletedFalse(2L));
    }

    @Test
    void countTipsAndTricksCommentByParentCommentIdAndDeletedFalseNotFoundTest() {
        assertEquals(0, tipsAndTricksCommentRepo.countTipsAndTricksCommentByParentCommentIdAndDeletedFalse(10L));
    }

    @Test
    void countLikesByCommentIdTest() {
        assertEquals(2, tipsAndTricksCommentRepo.countLikesByCommentId(2L));
    }

}
package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.EcoNewsComment;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/eco_news_comment.sql")
class EcoNewsCommentRepoTest {
    @Autowired
    private EcoNewsCommentRepo ecoNewsCommentRepo;

    @Test
    void findAllEcoNewsCommentsTest() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<EcoNewsComment> ecoNewsComments = ecoNewsCommentRepo
            .findAllByParentCommentIsNullAndEcoNewsIdOrderByCreatedDateDesc(
                pageRequest, 1L)
            .getContent();
        Long firstActual = ecoNewsComments.get(0).getId();
        Long secondActual = ecoNewsComments.get(1).getId();

        assertEquals(5L, firstActual);
        assertEquals(2L, secondActual);

    }

    @Test
    void findAllEcoNewsRepliesTest() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<EcoNewsComment> ecoNewsComments = ecoNewsCommentRepo
            .findAllByParentCommentIdOrderByCreatedDateDesc(pageRequest, 1L)
            .getContent();
        Long firstActual = ecoNewsComments.get(0).getId();
        Long secondActual = ecoNewsComments.get(1).getId();

        assertEquals(4L, firstActual);
        assertEquals(3L, secondActual);
    }

    @Test
    void countOfEcoNewsRepliesTest() {
        int expected = ecoNewsCommentRepo.countByParentCommentId(1L);
        assertEquals(2, expected);
    }

    @Test
    void countOfNotDeletedEcoNewsCommentsTest() {
        int expected = ecoNewsCommentRepo.countOfComments(2L);
        assertEquals(1, expected);
    }

    @Test
    void findAllActiveEcoNewsCommentsTest() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<EcoNewsComment> ecoNewsComments = ecoNewsCommentRepo
            .findAllByParentCommentIsNullAndDeletedFalseAndEcoNewsIdOrderByCreatedDateDesc(
                pageRequest, 1L)
            .getContent();
        Long expected = ecoNewsComments.get(0).getId();

        assertEquals(2L, expected);
        assertEquals(2, ecoNewsComments.size());
    }

    @Test
    void findAllActiveEcoNewsRepliesTest() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<EcoNewsComment> ecoNewsComments = ecoNewsCommentRepo
            .findAllByParentCommentIdAndDeletedFalseOrderByCreatedDateDesc(pageRequest, 2L)
            .getContent();
        Long expected = ecoNewsComments.get(0).getId();
        assertEquals(6L, expected);
    }
}
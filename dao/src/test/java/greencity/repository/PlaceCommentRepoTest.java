package greencity.repository;

import greencity.entity.Comment;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("classpath:sql/place_comment.sql")
class PlaceCommentRepoTest {

    @Autowired
    PlaceCommentRepo placeCommentRepo;

    @Test
    void findByIdTest() {
        Comment comment = placeCommentRepo.findById(1L).get();
        assertEquals(1, comment.getId());
    }

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Comment> comments = placeCommentRepo.findAll(pageable);
        List<Comment> collect = comments.get().collect(Collectors.toList());
        assertEquals(2, collect.size());
        assertEquals(1, collect.get(0).getId());
    }
}

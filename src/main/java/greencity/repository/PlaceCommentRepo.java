package greencity.repository;

import greencity.entity.Comment;
import java.awt.print.Pageable;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceCommentRepo extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPlaceId(Pageable pageable,Long placeId);

    Optional<Comment> findById(Long id);
}

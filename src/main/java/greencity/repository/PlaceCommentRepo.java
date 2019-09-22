package greencity.repository;

import greencity.entity.Comment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Comment} entity.
 */
@Repository
public interface PlaceCommentRepo extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPlaceId(Long placeId, Pageable pageable);

    Optional<Comment> findByPlaceIdAndUserId(Long placeId, Long userId);

    Optional<Comment> findById(Long id);
}

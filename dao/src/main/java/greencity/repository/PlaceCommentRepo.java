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
    /**
     * Method with return {@link Optional} of {@link Comment} by comment id.
     *
     * @param id of {@link Comment}.
     * @return {@link Optional} of {@link Comment} .
     * @author Marian Milian
     */
    Optional<Comment> findById(Long id);

    /**
     * Method with return {@link Page} of {@link Comment} - s.
     *
     * @param pageable pageable configuration.
     * @return {@link Page}.
     * @author Rostyslav Khasanov
     */
    Page<Comment> findAll(Pageable pageable);
}

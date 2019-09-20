package greencity.service.impl;

import greencity.entity.Comment;
import greencity.repository.PlaceCommentRepo;
import greencity.service.PlaceCommentService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code CommentService}.
 *
 * @author Marian Milian
 * @version 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class PlaceCommentServiceImpl implements PlaceCommentService {
    private PlaceCommentRepo placeCommentRepo;

    /**
     * {@inheritDoc}
     *
     * @author Marian Milian
     */
    @Override
    public List<Comment> findAllByPlaceId(Long placeId) {
        return placeCommentRepo.findAllByPlaceId(placeId);
    }

    /**
     * {@inheritDoc}
     *
     * @author Marian Milian
     */
    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.empty();
    }
}

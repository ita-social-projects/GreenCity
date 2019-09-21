package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.comment.CommentDto;
import greencity.entity.Comment;
import greencity.exception.NotFoundException;
import greencity.repository.PlaceCommentRepo;
import greencity.service.PlaceCommentService;
import greencity.service.PlaceService;
import java.security.Principal;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private PlaceService placeService;
    private ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     *
     * @author Marian Milian
     */
    @Override
    public PageableDto<Comment> findAllByPlaceId(Long placeId, Pageable pageable) {
        Page<Comment> comments = placeCommentRepo.findAllByPlaceId(placeService.findById(placeId).getId(), pageable);
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author Marian Milian
     */
    @Override
    public Optional<Comment> findById(Long id) {
        return placeCommentRepo.findById(id);
    }

    /**
     * {@inheritDoc}
     *
     * @author Marian Milian
     */
    @Override
    public Comment save(Long placeId, CommentDto commentDto, String email) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setPlace(placeService.findById(placeId));
        return placeCommentRepo.save(comment);
    }

    /**
     * {@inheritDoc}
     *
     * @author Marian Milian
     */
    @Override
    public Long deleteById(Long id) {
        Comment comment = findById(id).orElseThrow(() -> new NotFoundException("" + id));
        placeCommentRepo.delete(comment);
        return id;
    }
}

package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.entity.Comment;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.exception.NotFoundException;
import greencity.repository.PlaceCommentRepo;
import greencity.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private UserService userService;
    private PlaceCommentRepo placeCommentRepo;
    private PlaceService placeService;
    private RateService rateService;
    private PhotoService photoService;
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
    public CommentReturnDto findById(Long id) {
        Comment comment = placeCommentRepo.findById(id).orElseThrow(() -> new NotFoundException(""));
        return modelMapper.map(comment, CommentReturnDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Marian Milian
     */
    @Override
    public CommentReturnDto save(Long placeId, AddCommentDto addCommentDto, String email) {
        Place place = placeService.findById(placeId);
        User user = userService.findByEmail(email).orElseThrow(() -> new NotFoundException(""));
        Comment comment = modelMapper.map(addCommentDto, Comment.class);
        comment.setPlace(place);
        comment.setUser(user);
        comment.getRate().setUser(user);
        comment.getRate().setPlace(place);
        comment.getPhotos().forEach(photo -> {
            if (photoService.findByName(photo.getName()).isPresent()) {
                throw new NotFoundException("");
            }
            photo.setUser(user);
            photo.setComment(comment);
        });

        return modelMapper.map(placeCommentRepo.save(comment), CommentReturnDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Marian Milian
     */
    @Override
    public void deleteById(Long id) {
        placeCommentRepo.delete(placeCommentRepo.findById(id).orElseThrow(() -> new NotFoundException("")));
    }
}

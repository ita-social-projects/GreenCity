package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentAdminDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.entity.Comment;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.PlaceCommentRepo;
import greencity.service.PhotoService;
import greencity.service.PlaceCommentService;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
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
    private UserService userService;
    private PlaceCommentRepo placeCommentRepo;
    private PlaceService placeService;
    private PhotoService photoService;
    private ModelMapper modelMapper;

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
        User user = userService.findByEmail(email);
        Comment comment = modelMapper.map(addCommentDto, Comment.class);
        comment.setPlace(place);
        comment.setUser(user);
        if (comment.getEstimate() != null) {
            comment.getEstimate().setUser(user);
            comment.getEstimate().setPlace(place);
        }
        comment.getPhotos().forEach(photo -> {
            if (photoService.findByName(photo.getName()).isPresent()) {
                throw new BadRequestException(ErrorMessage.PHOTO_IS_PRESENT);
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
        placeCommentRepo.delete(placeCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto getAllComments(Pageable pageable) {
        Page<Comment> comments = placeCommentRepo.findAll(pageable);
        List<CommentAdminDto> commentList =
            comments.getContent()
                .stream().map(comment -> modelMapper.map(comment, CommentAdminDto.class))
                .collect(Collectors.toList());
        return new PageableDto<CommentAdminDto>(
            commentList,
            comments.getTotalElements(),
            comments.getPageable().getPageNumber()
        );
    }
}

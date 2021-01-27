package greencity.service;

import greencity.annotations.RatingCalculationEnum;
import greencity.client.RestClient;
import static greencity.constant.AppConstant.AUTHORIZATION;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentAdminDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserBlockedException;
import greencity.repository.PlaceCommentRepo;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private RestClient restClient;
    private PlaceCommentRepo placeCommentRepo;
    private PlaceService placeService;
    private PhotoService photoService;
    private ModelMapper modelMapper;
    private final greencity.rating.RatingCalculation ratingCalculation;
    private final HttpServletRequest httpServletRequest;

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
        UserVO userVO = restClient.findByEmail(email);
        if (userVO.getUserStatus().equals(UserStatus.BLOCKED)) {
            throw new UserBlockedException(ErrorMessage.USER_HAS_BLOCKED_STATUS);
        }
        Place place = modelMapper.map(placeService.findById(placeId), Place.class);
        User user = modelMapper.map(userVO, User.class);
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
            photo.setPlace(place);
        });
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
            () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.ADD_COMMENT, userVO, accessToken));
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserVO userVO = restClient.findByEmail(authentication.getName());
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
            () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.DELETE_COMMENT, userVO, accessToken));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<CommentAdminDto> getAllComments(Pageable pageable) {
        Page<Comment> comments = placeCommentRepo.findAll(pageable);
        List<CommentAdminDto> commentList =
            comments.getContent()
                .stream().map(comment -> modelMapper.map(comment, CommentAdminDto.class))
                .collect(Collectors.toList());
        return new PageableDto<>(
            commentList,
            comments.getTotalElements(),
            comments.getPageable().getPageNumber(),
            comments.getTotalPages());
    }
}

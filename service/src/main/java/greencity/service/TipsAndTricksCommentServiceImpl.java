package greencity.service;

import greencity.annotations.AchievementCalculation;
import greencity.annotations.RatingCalculation;
import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentDto;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.TipsAndTricks;
import greencity.entity.TipsAndTricksComment;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.TipsAndTricksCommentRepo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TipsAndTricksCommentServiceImpl implements TipsAndTricksCommentService {
    private TipsAndTricksCommentRepo tipsAndTricksCommentRepo;
    private TipsAndTricksService tipsAndTricksService;
    private ModelMapper modelMapper;

    /**
     * Method to save {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param tipsandtricksId                   id of
     *                                          {@link greencity.entity.TipsAndTricks}
     *                                          to which we save comment.
     * @param addTipsAndTricksCommentDtoRequest dto with
     *                                          {@link greencity.entity.TipsAndTricksComment}
     *                                          text, parentCommentId.
     * @param userVO                            {@link UserVO} that saves the
     *                                          comment.
     * @return {@link AddTipsAndTricksCommentDtoRequest} instance.
     */
    @RatingCalculation(rating = RatingCalculationEnum.ADD_COMMENT)
    @AchievementCalculation(category = "Tips&TricksComments")
    @Override
    public AddTipsAndTricksCommentDtoResponse save(Long tipsandtricksId,
        AddTipsAndTricksCommentDtoRequest addTipsAndTricksCommentDtoRequest,
        UserVO userVO) {
        TipsAndTricks tipsAndTricks = modelMapper.map(tipsAndTricksService
            .findById(tipsandtricksId), TipsAndTricks.class);
        TipsAndTricksComment tipsAndTricksComment =
            modelMapper.map(addTipsAndTricksCommentDtoRequest, TipsAndTricksComment.class);
        tipsAndTricksComment.setUser(modelMapper.map(userVO, User.class));
        tipsAndTricksComment.setTipsAndTricks(tipsAndTricks);
        if (addTipsAndTricksCommentDtoRequest.getParentCommentId() != null
            && addTipsAndTricksCommentDtoRequest.getParentCommentId() != 0) {
            TipsAndTricksComment parentComment =
                tipsAndTricksCommentRepo
                    .findById(addTipsAndTricksCommentDtoRequest.getParentCommentId())
                    .orElseThrow(() -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
            if (parentComment.isDeleted()) {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_TO_DELETED_COMMENT);
            }
            if (parentComment.getParentComment() == null
                && parentComment.getTipsAndTricks().getId().equals(tipsandtricksId)) {
                tipsAndTricksComment.setParentComment(parentComment);
            } else {
                if (parentComment.getParentComment() != null) {
                    throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
                } else if (!parentComment.getTipsAndTricks().getId().equals(tipsandtricksId)) {
                    throw new BadRequestException(ErrorMessage.CANNOT_REPLY_WITH_OTHER_DIFFERENT_TIPSANDTRICKS_ID);
                }
            }
        }

        return modelMapper
            .map(tipsAndTricksCommentRepo.save(tipsAndTricksComment), AddTipsAndTricksCommentDtoResponse.class);
    }

    /**
     * Method returns all comments to certain tipsAndTricks specified by
     * tipsAndTricksId.
     *
     * @param userVO          current {@link UserVO}
     * @param tipsAndTricksId specifies {@link greencity.entity.TipsAndTricks} to
     *                        which we search for comments
     * @return all comments to certain tipsAndTricksId specified by tipsAndTricksId.
     */
    @Override
    public PageableDto<TipsAndTricksCommentDto> findAllComments(Pageable pageable, UserVO userVO,
        Long tipsAndTricksId) {
        Page<TipsAndTricksComment> pages =
            tipsAndTricksCommentRepo.findAllByParentCommentIsNullAndTipsAndTricksIdOrderByCreatedDateDesc(
                pageable, tipsAndTricksId);
        List<TipsAndTricksCommentDto> tipsAndTricksCommentDtos = pages
            .stream()
            .map(comment -> {
                comment.setCurrentUserLiked(comment.getUsersLiked().contains(modelMapper.map(userVO, User.class)));
                return comment;
            })
            .map(tipsAndTricksComment -> modelMapper.map(tipsAndTricksComment, TipsAndTricksCommentDto.class))
            .map(comment -> {
                comment
                    .setReplies(tipsAndTricksCommentRepo
                        .countTipsAndTricksCommentByParentCommentIdAndDeletedFalse(comment.getId()));
                return comment;
            })
            .collect(Collectors.toList());

        return new PageableDto<>(
            tipsAndTricksCommentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * Method returns all replies to certain comment specified by parentCommentId.
     *
     * @param parentCommentId specifies
     *                        {@link greencity.entity.TipsAndTricksComment} to which
     *                        we search for replies
     * @return all replies to certain comment specified by parentCommentId.
     */
    @Override
    public List<TipsAndTricksCommentDto> findAllReplies(Long parentCommentId) {
        return tipsAndTricksCommentRepo.findAllByParentCommentIdAndDeletedFalseOrderByCreatedDateAsc(parentCommentId)
            .stream()
            .map(tipsAndTricksComment -> modelMapper
                .map(tipsAndTricksComment, TipsAndTricksCommentDto.class))
            .collect(Collectors.toList());
    }

    /**
     * Method to mark {@link greencity.entity.TipsAndTricksComment} specified by id
     * as deleted.
     *
     * @param id     of {@link greencity.entity.TipsAndTricksComment} to delete.
     * @param userVO current {@link UserVO} that wants to delete.
     */
    @RatingCalculation(rating = RatingCalculationEnum.DELETE_COMMENT)
    @Override
    public void deleteById(Long id, UserVO userVO) {
        TipsAndTricksComment comment = tipsAndTricksCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));

        if (userVO.getRole() != Role.ROLE_ADMIN && !userVO.getId().equals(comment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        if (comment.getComments() != null) {
            comment.getComments().forEach(c -> c.setDeleted(true));
        }
        comment.setDeleted(true);
        tipsAndTricksCommentRepo.save(comment);
    }

    /**
     * Method to change the existing {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param text   new text of {@link greencity.entity.TipsAndTricksComment}.
     * @param id     to specify {@link greencity.entity.TipsAndTricksComment} that
     *               user wants to change.
     * @param userVO current {@link UserVO} that wants to change.
     */
    @Override
    public void update(String text, Long id, UserVO userVO) {
        TipsAndTricksComment comment = tipsAndTricksCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (!userVO.getId().equals(comment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }
        comment.setText(text);
        tipsAndTricksCommentRepo.save(comment);
    }

    /**
     * Method to like or dislike {@link greencity.entity.TipsAndTricksComment}
     * specified by id.
     *
     * @param id     of {@link greencity.entity.TipsAndTricksComment} to
     *               like/dislike.
     * @param userVO current {@link UserVO} that wants to like/dislike.
     */
    @Override
    public void like(Long id, UserVO userVO) {
        TipsAndTricksComment comment = tipsAndTricksCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        TipsAndTricksCommentVO commentVO = modelMapper.map(comment, TipsAndTricksCommentVO.class);
        if (comment.getUsersLiked().stream()
            .anyMatch(user -> user.getId().equals(userVO.getId()))) {
            tipsAndTricksService.unlikeComment(userVO, commentVO);
        } else {
            tipsAndTricksService.likeComment(userVO, commentVO);
        }
        tipsAndTricksCommentRepo.save(comment);
    }

    /**
     * Method returns count of likes to certain
     * {@link greencity.entity.TipsAndTricksComment} specified by id.
     *
     * @param id of {@link greencity.entity.TipsAndTricksComment} to which we get
     *           count of likes.
     * @return count of likes to certain
     *         {@link greencity.entity.TipsAndTricksComment} specified by id.
     */
    @Override
    public int countLikes(Long id) {
        return tipsAndTricksCommentRepo.countLikesByCommentId(id);
    }

    /**
     * Method to count replies to certain
     * {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param id specifies parent comment to all replies
     * @return amount of replies
     */
    @Override
    public int countReplies(Long id) {
        return tipsAndTricksCommentRepo.countTipsAndTricksCommentByParentCommentIdAndDeletedFalse(id);
    }

    /**
     * Method to count replies to certain
     * {@link greencity.entity.TipsAndTricksComment}.
     *
     * @param tipAndTricksId specifies parent comment to all replies
     * @return amount of replies
     */
    @Override
    public int countComments(Long tipAndTricksId) {
        return tipsAndTricksCommentRepo.countAllByTipsAndTricksId(tipAndTricksId);
    }
}

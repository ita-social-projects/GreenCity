package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.EmailNotificationMessagesConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.CommentAuthorDto;
import greencity.dto.comment.CommentDto;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.Habit;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.enums.*;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.message.GeneralEmailMessage;
import greencity.rating.RatingCalculation;
import greencity.repository.CommentRepo;
import greencity.repository.HabitRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final HabitRepo habitRepo;
    private final UserRepo userRepo;
    private final CommentRepo commentRepo;
    private final ModelMapper modelMapper;
    private final RatingCalculation ratingCalculation;
    private final AchievementCalculation achievementCalculation;

    /**
     * {@inheritDoc}
     */
    @Override
    public AddCommentDtoResponse save(ArticleType articleType, Long articleId,
                                      AddCommentDtoRequest addCommentDtoRequest, UserVO userVO) {
        User articleAuthor = articleCheckIfExistsAndReturnAuthor(articleType, articleId);
        // todo check notifications for articleAuthor
        System.out.println(articleAuthor);

        Comment comment = modelMapper.map(addCommentDtoRequest, Comment.class);
        comment.setArticleType(articleType);
        comment.setArticleId(articleId);
        comment.setUser(modelMapper.map(userVO, User.class));
        comment.setStatus(CommentStatus.ORIGINAL);

        if(addCommentDtoRequest.getParentCommentId() != null && addCommentDtoRequest.getParentCommentId() > 0){
            Long parentCommentId = addCommentDtoRequest.getParentCommentId();
            Comment parentComment = commentRepo.findById(parentCommentId)
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + parentCommentId));

            if (parentComment.getParentComment() != null) {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }

            if (!parentComment.getArticleId().equals(articleId)) {
                String message = ErrorMessage.COMMENT_NOT_FOUND_BY_ID + parentCommentId
                        + " in " + articleType.getName() + " with id: " + articleId;
                throw new NotFoundException(message);
            }
            comment.setParentComment(parentComment);
        }

        ratingCalculation.ratingCalculation(RatingCalculationEnum.COMMENT_OR_REPLY, userVO);
        achievementCalculation.calculateAchievement(userVO,
                AchievementCategoryType.COMMENT_OR_REPLY, AchievementAction.ASSIGN);

        AddCommentDtoResponse addCommentDtoResponse = modelMapper.map(
                commentRepo.save(comment), AddCommentDtoResponse.class);
        addCommentDtoResponse.setAuthor(modelMapper.map(userVO, CommentAuthorDto.class));
        return addCommentDtoResponse;
    }

    private User articleCheckIfExistsAndReturnAuthor(ArticleType articleType, Long articleId) {
        if(articleType == ArticleType.HABIT) {
            Habit habit = habitRepo.findById(articleId).orElseThrow(() -> new NotFoundException("Habit not found"));
            return userRepo.findById(habit.getUserId()).orElseThrow(() -> new NotFoundException("User not found"));
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommentDto findCommentById(Long id, UserVO userVO) {
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + id));

        if (userVO != null) {
            comment.setCurrentUserLiked(comment.getUsersLiked().stream()
                    .anyMatch(u -> u.getId().equals(userVO.getId())));
        }
        // todo check how Comments list works
        return modelMapper.map(comment, CommentDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<CommentDto> findAllActiveReplies(Pageable pageable, Long parentCommentId, UserVO userVO) {
        Page<Comment> pages =
                commentRepo.findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageable, parentCommentId,
                        CommentStatus.DELETED);

        if (userVO != null) {
            pages.forEach(ec -> ec.setCurrentUserLiked(ec.getUsersLiked().stream()
                    .anyMatch(u -> u.getId().equals(userVO.getId()))));
        }

        List<CommentDto> commentDtos = pages.stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());

        return new PageableDto<>(
                commentDtos,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages());
    }

    @Override
    public int countComments(ArticleType type, Long articleId) {
        if(type == ArticleType.HABIT){
            Habit habit = habitRepo.findById(articleId).orElseThrow(() ->
                    new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + articleId));
            return commentRepo.countNotDeletedCommentsByHabit(habit.getId());

        }
        return 0;
    }

    /**
     * Method to count not deleted replies to the certain {@link Comment}.
     *
     * @param parentCommentId to specify parent comment {@link Comment}
     * @return amount of replies
     */
    @Override
    public int countAllActiveReplies(Long parentCommentId) {
        if (commentRepo.findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED).isEmpty()) {
            throw new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + parentCommentId);
        }
        return commentRepo.countByParentCommentIdAndStatusNot(parentCommentId, CommentStatus.DELETED);
    }

    @Override
    public PageableDto<CommentDto> getAllActiveComments(Pageable pageable, UserVO userVO, Long articleId,
                                                        ArticleType articleType) {
        if(articleType == ArticleType.HABIT){
            Optional<Habit> habit = habitRepo.findById(articleId);
            if(habit.isEmpty()){
                throw new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + articleId);
            }
        }

        Page<Comment> pages =
                commentRepo.findAllByParentCommentIdIsNullAndArticleIdAndArticleTypeAndStatusNotOrderByCreatedDateDesc
                        (pageable, articleId, articleType, CommentStatus.DELETED);

        if (userVO != null) {
            pages.forEach(comment -> comment.setCurrentUserLiked(comment.getUsersLiked()
                    .stream()
                    .anyMatch(u -> u.getId().equals(userVO.getId()))));
        }

        List<CommentDto> commentDto = pages
                .stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());

        return new PageableDto<>(
                commentDto,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages());
    }
}

package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.EmailNotificationMessagesConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.CommentAuthorDto;
import greencity.dto.comment.CommentDto;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.EcoNewsComment;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public CommentDto findCommentById(ArticleType type, Long id, UserVO userVO) {
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + id));

        if(comment.getArticleType() != type) {
            throw new BadRequestException("Comment with id: " + id + " doesn't belongs to " + type.getLink());
        }

        if (userVO != null) {
            comment.setCurrentUserLiked(comment.getUsersLiked().stream()
                    .anyMatch(u -> u.getId().equals(userVO.getId())));
        }

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
     * {@inheritDoc}
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

        List<CommentDto> commentDtos = convertToCommentDtos(pages, userVO).stream()
                .map(this::setRepliesToComment)
                .toList();


        return new PageableDto<>(
                commentDtos,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages());
    }

    private List<CommentDto> convertToCommentDtos(Page<Comment> pages, UserVO user) {
        return pages.stream()
                .map(comment -> {
                    CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
                    commentDto.setCurrentUserLiked(comment.getUsersLiked().stream()
                            .anyMatch(u -> u.getId().equals(user.getId())));
                    commentDto.setAuthor(modelMapper.map(user, CommentAuthorDto.class));
                    commentDto.setLikes(comment.getUsersLiked().size());
                    commentDto.setReplies(comment.getComments().size());
                    commentDto.setStatus(comment.getStatus().name());
                    return commentDto;
                })
                .toList();
    }

    private CommentDto setRepliesToComment(CommentDto comment) {
        comment.setReplies(commentRepo.countByParentCommentId(comment.getId()));
        return comment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(Long commentId, UserVO userVO) {
        Comment comment = commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));
        if (comment.getUsersLiked().stream().anyMatch(user -> user.getId().equals(userVO.getId()))) {
            comment.getUsersLiked().removeIf(user -> user.getId().equals(userVO.getId()));
            ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_LIKE_COMMENT_OR_REPLY, userVO);
            achievementCalculation.calculateAchievement(userVO,
                    AchievementCategoryType.LIKE_COMMENT_OR_REPLY, AchievementAction.DELETE);
        } else {
            comment.getUsersLiked().add(modelMapper.map(userVO, User.class));
            achievementCalculation.calculateAchievement(userVO,
                    AchievementCategoryType.LIKE_COMMENT_OR_REPLY, AchievementAction.ASSIGN);
            ratingCalculation.ratingCalculation(RatingCalculationEnum.LIKE_COMMENT_OR_REPLY, userVO);
        }
        commentRepo.save(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AmountCommentLikesDto countLikes(Long commentId, UserVO userVO) {
        Comment comment = commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED).orElseThrow(
                () -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));

        boolean isLiked =
                userVO != null && comment.getUsersLiked().stream().anyMatch(u -> u.getId().equals(userVO.getId()));
        return AmountCommentLikesDto.builder()
                .id(comment.getId())
                .userId(userVO == null ? 0 : userVO.getId())
                .isLiked(isLiked)
                .amountLikes(comment.getUsersLiked().size())
                .build();
    }
}

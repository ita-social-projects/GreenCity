package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.ErrorMessage;
import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.CommentAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.Habit;
import greencity.entity.User;
import greencity.entity.event.EventComment;
import greencity.enums.*;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.rating.RatingCalculation;
import greencity.repository.CommentRepo;
import greencity.repository.HabitRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
                        + " in " + articleType.getDescription() + " with id: " + articleId;
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
}

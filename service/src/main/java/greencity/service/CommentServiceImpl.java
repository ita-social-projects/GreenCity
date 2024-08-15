package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.EmailNotificationMessagesConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.CommentAuthorDto;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.Habit;
import greencity.entity.User;
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
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final HabitRepo habitRepo;
    private final UserRepo userRepo;
    private final CommentRepo commentRepo;
    private final ModelMapper modelMapper;
    private final RatingCalculation ratingCalculation;
    private final AchievementCalculation achievementCalculation;
    private final NotificationService notificationService;
    @Value("${client.address}")
    private String clientAddress;

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

        sendNotificationToTaggedUser(articleType, articleId, userVO, addCommentDtoRequest.getText());

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
     * Method to send email notification if user tagged in comment.
     *
     * @param articleId {@link Long} article id.
     * @param userVO  {@link UserVO} comment author.
     * @param comment {@link String} comment.
     */
    private void sendNotificationToTaggedUser(ArticleType type, Long articleId, UserVO userVO, String comment) {
        Long userId = getUserIdFromComment(comment);
        if (userId != null) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
            notificationService.sendEmailNotification(GeneralEmailMessage.builder()
                    .email(user.getEmail())
                    .subject(String.format(EmailNotificationMessagesConstants.ARTICLE_TAGGED_SUBJECT, userVO.getName()))
                    .message(clientAddress + "/#/" + type.getLink() + "/" + articleId)
                    .build());
        }
    }

    /**
     * Method to extract user id from comment.
     *
     * @param message comment from {@link AddCommentDtoResponse}.
     * @return user id if present or null.
     */
    private Long getUserIdFromComment(String message) {
        String regEx = "data-userid=\"(\\d+)\"";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }
        return null;
    }
}

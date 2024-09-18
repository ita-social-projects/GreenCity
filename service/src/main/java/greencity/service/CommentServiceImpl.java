package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.comment.*;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.entity.event.Event;
import greencity.enums.*;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.message.UserTaggedInCommentMessage;
import greencity.rating.RatingCalculation;
import greencity.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static greencity.constant.ErrorMessage.HABIT_NOT_FOUND_BY_ID;
import static greencity.constant.ErrorMessage.EVENT_NOT_FOUND_BY_ID;
import static greencity.constant.ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID;
import static greencity.constant.ErrorMessage.USER_NOT_FOUND_BY_ID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final HabitRepo habitRepo;
    private final EventRepo eventRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final UserRepo userRepo;
    private final CommentRepo commentRepo;
    private final HabitTranslationRepo habitTranslationRepo;
    private final ModelMapper modelMapper;
    private final RatingCalculation ratingCalculation;
    private final AchievementCalculation achievementCalculation;
    private final UserNotificationService userNotificationService;
    private final NotificationService notificationService;
    @Value("${client.address}")
    private String clientAddress;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AddCommentDtoResponse save(ArticleType articleType, Long articleId,
        AddCommentDtoRequest addCommentDtoRequest, UserVO userVO, Locale locale) {
        if (getArticleAuthor(articleType, articleId) == null) {
            throw new NotFoundException("Article author not found");
        }

        Comment comment = modelMapper.map(addCommentDtoRequest, Comment.class);
        comment.setArticleType(articleType);
        comment.setArticleId(articleId);
        comment.setUser(modelMapper.map(userVO, User.class));
        comment.setStatus(CommentStatus.ORIGINAL);

        if (addCommentDtoRequest.getParentCommentId() != null && addCommentDtoRequest.getParentCommentId() > 0) {
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
            createCommentReplyNotification(articleType, articleId, comment,
                modelMapper.map(parentComment.getUser(), UserVO.class), locale);
        }

        ratingCalculation.ratingCalculation(RatingCalculationEnum.COMMENT_OR_REPLY, userVO);
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.COMMENT_OR_REPLY, AchievementAction.ASSIGN);

        AddCommentDtoResponse addCommentDtoResponse = modelMapper.map(
            commentRepo.save(comment), AddCommentDtoResponse.class);
        addCommentDtoResponse.setAuthor(modelMapper.map(userVO, CommentAuthorDto.class));

        createCommentNotification(articleType, articleId, comment, userVO, locale);
        sendNotificationToTaggedUser(modelMapper.map(comment, CommentVO.class), articleType, userVO, locale);

        return addCommentDtoResponse;
    }

    /**
     * Sends a notification to users tagged in a comment on a specific article.
     *
     * @param commentVO   the comment containing the tag, {@link CommentVO}.
     * @param articleType the type of the article where the comment is made,
     *                    {@link ArticleType}.
     * @param userVO      the user who made the comment, {@link UserVO}.
     * @param locale      the locale used for localization of the notification,
     *                    {@link Locale}.
     *
     * @throws NotFoundException if a tagged user is not found by ID.
     */
    private void sendNotificationToTaggedUser(CommentVO commentVO, ArticleType articleType, UserVO userVO,
        Locale locale) {
        String commentText = commentVO.getText();
        Set<Long> usersId = getUserIdFromComment(commentText);
        if (!usersId.isEmpty()) {
            for (Long userId : usersId) {
                User user = userRepo.findById(userId)
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
                UserTaggedInCommentMessage message = UserTaggedInCommentMessage.builder()
                    .commentedElementId(commentVO.getArticleId())
                    .elementName(commentVO.getText())
                    .taggerName(userVO.getName())
                    .language(locale.getLanguage())
                    .creationDate(commentVO.getCreatedDate())
                    .receiverName(user.getName())
                    .receiverEmail(user.getEmail())
                    .commentText(commentText)
                    .baseLink(getBaseLink(articleType, commentVO.getArticleId(), userVO.getId()))
                    .build();
                notificationService.sendUsersTaggedInCommentEmailNotification(message);
                createUserTagInCommentsNotification(articleType, commentVO.getArticleId(),
                    modelMapper.map(commentVO, Comment.class),
                    modelMapper.map(user, UserVO.class),
                    locale);
            }
        }
    }

    /**
     * Method to extract user id from comment.
     *
     * @param message - comment
     * @return user id if present or null.
     */
    private Set<Long> getUserIdFromComment(String message) {
        String regEx = "data-userid=\"(\\d+)\"";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(message);
        Set<Long> userIds = new HashSet<>();
        if (!matcher.find()) {
            return userIds;
        }
        matcher.reset();
        while (matcher.find()) {
            userIds.add(Long.valueOf(matcher.group(1)));
        }
        return userIds;
    }

    private String getBaseLink(ArticleType articleType, Long articleId, Long userId) {
        switch (articleType) {
            case HABIT:
                return clientAddress + "/#/profile/" + userId + "/allhabits/addhabit/" + articleId;

            default:
                throw new BadRequestException(ErrorMessage.UNSUPPORTED_ARTICLE_TYPE);
        }
    }

    /**
     * Method to return author of article.
     *
     * @param articleType {@link ArticleType}.
     * @param articleId   {@link Long} id of an article.
     *
     * @return article author {@link User}.
     */
    protected User getArticleAuthor(ArticleType articleType, Long articleId) {
        Long articleAuthorId;
        switch (articleType) {
            case HABIT:
                Habit habit = habitRepo.findById(articleId)
                    .orElseThrow(() -> new NotFoundException(HABIT_NOT_FOUND_BY_ID + articleId));
                articleAuthorId = habit.getUserId();
                break;

            case EVENT:
                Event event = eventRepo.findById(articleId)
                    .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND_BY_ID + articleId));
                articleAuthorId = event.getOrganizer().getId();
                break;

            case ECO_NEWS:
                EcoNews ecoNews = ecoNewsRepo.findById(articleId)
                    .orElseThrow(() -> new NotFoundException(ECO_NEWS_NOT_FOUND_BY_ID + articleId));
                articleAuthorId = ecoNews.getAuthor().getId();
                break;

            default:
                throw new BadRequestException(ErrorMessage.UNSUPPORTED_ARTICLE_TYPE);
        }
        return userRepo.findById(articleAuthorId)
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_BY_ID + articleAuthorId));
    }

    /**
     * Method to return title of article.
     *
     * @param articleType {@link ArticleType}.
     * @param articleId   {@link Long} id of an article.
     *
     * @return article title {@link User}.
     */
    protected String getArticleTitle(ArticleType articleType, Long articleId, Locale locale) {
        String articleName;
        switch (articleType) {
            case HABIT:
                Habit habit = habitRepo.findById(articleId)
                    .orElseThrow(() -> new NotFoundException(HABIT_NOT_FOUND_BY_ID + articleId));
                HabitTranslation habitTranslation =
                    habitTranslationRepo.findByHabitAndLanguageCode(habit, locale.getLanguage())
                        .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_TRANSLATION_NOT_FOUND + articleId));
                articleName = habitTranslation.getName();
                break;

            default:
                throw new BadRequestException(ErrorMessage.UNSUPPORTED_ARTICLE_TYPE);
        }
        return articleName;
    }

    /**
     * Generic method for creating a notification for various comment-related
     * actions on an article.
     *
     * @param articleType      the type of the article, {@link ArticleType}.
     * @param articleId        the ID of the article, {@link Long}.
     * @param comment          the comment that triggered the notification,
     *                         {@link Comment}.
     * @param receiver         the user receiving the notification, {@link UserVO}.
     * @param sender           the user sending the notification, {@link UserVO}.
     * @param notificationType the type of notification, {@link NotificationType}.
     *
     * @throws BadRequestException if the article type is not supported.
     */
    private void createNotification(ArticleType articleType, Long articleId, Comment comment, UserVO receiver,
        UserVO sender, NotificationType notificationType, Locale locale) {
        userNotificationService.createNotification(
            receiver,
            sender,
            notificationType,
            articleId,
            getArticleTitle(articleType, articleId, locale),
            comment.getId(),
            comment.getText());
    }

    /**
     * Determines the appropriate notification type based on article and action
     * type.
     *
     * @param articleType the type of the article, {@link ArticleType}.
     * @param actionType  the type of action (COMMENT, REPLY, TAG).
     * @return the corresponding {@link NotificationType}.
     */
    private NotificationType getNotificationType(ArticleType articleType, CommentActionType actionType) {
        switch (articleType) {
            case HABIT:
                return switch (actionType) {
                    case COMMENT -> NotificationType.HABIT_COMMENT;
                    case COMMENT_REPLY -> NotificationType.HABIT_COMMENT_REPLY;
                    case COMMENT_USER_TAG -> NotificationType.HABIT_COMMENT_USER_TAG;
                    default -> throw new BadRequestException(ErrorMessage.UNSUPPORTED_ACTION_TYPE);
                };

            default:
                throw new BadRequestException(ErrorMessage.UNSUPPORTED_ARTICLE_TYPE);
        }
    }

    /**
     * Creates a notification for a comment on an article.
     *
     * @param articleType the type of the article, {@link ArticleType}.
     * @param articleId   the ID of the article, {@link Long}.
     * @param comment     the comment that was made, {@link Comment}.
     * @param userVO      the user who made the comment, {@link UserVO}.
     * @param locale      the locale used for localization of the notification,
     *                    {@link Locale}.
     */
    private void createCommentNotification(ArticleType articleType, Long articleId, Comment comment, UserVO userVO,
        Locale locale) {
        createNotification(articleType, articleId, comment,
            modelMapper.map(getArticleAuthor(articleType, articleId), UserVO.class),
            userVO, getNotificationType(articleType, CommentActionType.COMMENT), locale);
    }

    /**
     * Creates a notification for a reply to a comment.
     *
     * @param articleType the type of the article, {@link ArticleType}.
     * @param articleId   the ID of the article, {@link Long}.
     * @param comment     the comment that was made, {@link Comment}.
     * @param receiver    the user who receive a notification, {@link UserVO}.
     * @param locale      the locale used for localization of the notification,
     *                    {@link Locale}.
     */
    private void createCommentReplyNotification(ArticleType articleType, Long articleId, Comment comment,
        UserVO receiver, Locale locale) {
        createNotification(articleType, articleId, comment, receiver,
            modelMapper.map(getArticleAuthor(articleType, articleId), UserVO.class),
            getNotificationType(articleType, CommentActionType.COMMENT_REPLY), locale);
    }

    /**
     * Creates a notification for tagging a user in a comment.
     *
     * @param articleType the type of the article, {@link ArticleType}.
     * @param articleId   the ID of the article, {@link Long}.
     * @param comment     the comment where the user is tagged, {@link Comment}.
     * @param receiver    the user who is tagged in the comment, {@link UserVO}.
     * @param locale      the locale used for localization of the notification,
     *                    {@link Locale}.
     */
    private void createUserTagInCommentsNotification(ArticleType articleType, Long articleId, Comment comment,
        UserVO receiver, Locale locale) {
        createNotification(articleType, articleId, comment, receiver,
            modelMapper.map(getArticleAuthor(articleType, articleId), UserVO.class),
            getNotificationType(articleType, CommentActionType.COMMENT_USER_TAG), locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentDto getCommentById(ArticleType type, Long id, UserVO userVO) {
        Comment comment = commentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + id));

        if (comment.getArticleType() != type) {
            throw new BadRequestException("Comment with id: " + id + " doesn't belong to " + type.getLink());
        }

        if (userVO != null) {
            comment.setCurrentUserLiked(comment.getUsersLiked().stream()
                .anyMatch(u -> u.getId().equals(userVO.getId())));
        }

        return convertToCommentDto(comment, userVO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<CommentDto> getAllActiveReplies(Pageable pageable, Long parentCommentId, UserVO userVO) {
        Page<Comment> pages =
            commentRepo.findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageable, parentCommentId,
                CommentStatus.DELETED);

        pages = setCurrentUserLiked(pages, userVO);

        return convertPagesToCommentDtos(pages, userVO);
    }

    /**
     * Updates each {@link Comment} in a {@link Page} to set whether the current
     * user has liked the comment.
     *
     * @param pages  the {@link Page} of {@link Comment} entities to be updated.
     * @param userVO the {@link UserVO} representing the current user. This may be
     *               {@code null}, in which case no updates will be made to the
     *               comments' "current user liked" status.
     * @return the same {@link Page} of {@link Comment} with updated "current user
     *         liked" status for each comment.
     */
    public Page<Comment> setCurrentUserLiked(Page<Comment> pages, UserVO userVO) {
        if (userVO != null) {
            pages.forEach(ec -> ec.setCurrentUserLiked(ec.getUsersLiked().stream()
                .anyMatch(u -> u.getId().equals(userVO.getId()))));
        }
        return pages;
    }

    /**
     * Converts a {@link Page} of {@link Comment} entities into a
     * {@link PageableDto} containing {@link CommentDto} objects.
     *
     * @param pages  the {@link Page} of {@link Comment} entities to be converted.
     * @param userVO the {@link UserVO} representing the current user, used to
     *               determine if the user has liked each comment. This may be
     *               {@code null} if the current user's information is not
     *               available.
     * @return a {@link PageableDto} of {@link CommentDto} containing the mapped
     *         {@link CommentDto} objects.
     */
    public PageableDto<CommentDto> convertPagesToCommentDtos(Page<Comment> pages, UserVO userVO) {
        List<CommentDto> commentDtos = pages.getContent().stream().map(c -> convertToCommentDto(c, userVO)).toList();

        return new PageableDto<>(
            commentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countCommentsForHabit(Long habitId) {
        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new NotFoundException(HABIT_NOT_FOUND_BY_ID + habitId));
        return commentRepo.countNotDeletedCommentsByHabit(habit.getId());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<CommentDto> getAllActiveComments(Pageable pageable, UserVO userVO, Long articleId,
        ArticleType articleType) {
        if (articleType == ArticleType.HABIT) {
            Optional<Habit> habit = habitRepo.findById(articleId);
            if (habit.isEmpty()) {
                throw new NotFoundException(HABIT_NOT_FOUND_BY_ID + articleId);
            }
        }

        Page<Comment> pages =
            commentRepo.findAllByParentCommentIdIsNullAndArticleIdAndArticleTypeAndStatusNotOrderByCreatedDateDesc(
                pageable, articleId, articleType, CommentStatus.DELETED);

        pages = setCurrentUserLiked(pages, userVO);

        return convertPagesToCommentDtos(pages, userVO);
    }

    /**
     * Converts a {@link Comment} entity into a {@link CommentDto} data transfer
     * object.
     *
     * @param comment the {@link Comment} entity to be converted.
     * @param user    the {@link UserVO} representing the current user, which is
     *                used to determine if the user has liked the comment.
     * @return a {@link CommentDto} that contains the mapped information from the
     *         provided {@link Comment} entity.
     */
    private CommentDto convertToCommentDto(Comment comment, UserVO user) {
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        if (user != null) {
            commentDto.setCurrentUserLiked(comment.getUsersLiked().stream()
                .anyMatch(u -> u.getId().equals(user.getId())));
        }
        if (comment.getParentComment() != null) {
            commentDto.setParentCommentId(comment.getParentComment().getId());
        }
        commentDto.setAuthor(modelMapper.map(comment.getUser(), CommentAuthorDto.class));
        commentDto.setLikes(comment.getUsersLiked().size());
        commentDto.setReplies(comment.getComments().size());
        commentDto.setStatus(comment.getStatus().name());
        return commentDto;
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void update(String commentText, Long id, UserVO userVO) {
        Comment comment = commentRepo.findByIdAndStatusNot(id, CommentStatus.DELETED)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));

        if (!userVO.getId().equals(comment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }

        comment.setText(commentText);
        comment.setStatus(CommentStatus.EDITED);
        commentRepo.save(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void delete(Long id, UserVO userVO) {
        Comment comment = commentRepo
            .findByIdAndStatusNot(id, CommentStatus.DELETED)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + id));

        if (userVO.getRole() != Role.ROLE_ADMIN && !userVO.getId().equals(comment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        comment.setStatus(CommentStatus.DELETED);
        if (comment.getComments() != null) {
            comment.getComments()
                .forEach(c -> c.setStatus(CommentStatus.DELETED));
        }
        ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_COMMENT_OR_REPLY, userVO);
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.COMMENT_OR_REPLY, AchievementAction.DELETE);

        commentRepo.save(comment);
    }
}

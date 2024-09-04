package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.user.UserSearchDto;
import greencity.dto.user.UserTagDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.AchievementAction;
import greencity.enums.AchievementCategoryType;
import greencity.enums.CommentStatus;
import greencity.enums.NotificationType;
import greencity.enums.RatingCalculationEnum;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.rating.RatingCalculation;
import greencity.repository.EcoNewsCommentRepo;
import greencity.repository.EcoNewsRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EcoNewsCommentServiceImpl implements EcoNewsCommentService {
    private EcoNewsCommentRepo ecoNewsCommentRepo;
    private EcoNewsService ecoNewsService;
    private final AchievementCalculation achievementCalculation;
    private ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final RatingCalculation ratingCalculation;
    private final UserNotificationService userNotificationService;
    private final EcoNewsRepo ecoNewsRepo;
    private final UserRepo userRepo;
    private final NotificationService notificationService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AddEcoNewsCommentDtoResponse save(Long ecoNewsId, AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest,
        UserVO userVO) {
        EcoNewsVO ecoNewsVO = ecoNewsService.findById(ecoNewsId);
        EcoNewsComment ecoNewsComment = modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class);
        User user = modelMapper.map(userVO, User.class);
        ecoNewsComment.setUser(user);
        ecoNewsComment.setEcoNews(modelMapper.map(ecoNewsVO, EcoNews.class));
        if (addEcoNewsCommentDtoRequest.getParentCommentId() != 0) {
            EcoNewsComment parentComment =
                ecoNewsCommentRepo.findById(addEcoNewsCommentDtoRequest.getParentCommentId())
                    .orElseThrow(() -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
            if (parentComment.getParentComment() == null) {
                ecoNewsComment.setParentComment(parentComment);
                userNotificationService.createNotification(modelMapper.map(parentComment.getUser(), UserVO.class),
                    userVO, NotificationType.ECONEWS_COMMENT_REPLY, parentComment.getId(), parentComment.getText(),
                    ecoNewsId, ecoNewsVO.getTitle());
            } else {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }
        }
        achievementCalculation
            .calculateAchievement(userVO, AchievementCategoryType.COMMENT_OR_REPLY, AchievementAction.ASSIGN);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.COMMENT_OR_REPLY, userVO);
        ecoNewsComment.setStatus(CommentStatus.ORIGINAL);
        userNotificationService.createNotification(ecoNewsVO.getAuthor(), userVO, NotificationType.ECONEWS_COMMENT,
            ecoNewsId, ecoNewsVO.getTitle());
        return modelMapper.map(ecoNewsCommentRepo.save(ecoNewsComment), AddEcoNewsCommentDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<EcoNewsCommentDto> findAllReplies(Pageable pageable, Long ecoNewsId, Long parentCommentId,
        List<CommentStatus> statuses, UserVO userVO) {
        Page<EcoNewsComment> pages = ecoNewsCommentRepo
            .findAllByParentCommentIdAndStatusInOrderByCreatedDateDesc(pageable, parentCommentId, statuses);
        if (pages.isEmpty()
            || pages.get().findFirst().filter(c -> c.getEcoNews().getId().equals(ecoNewsId)).isEmpty()) {
            throw new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION);
        }
        UserVO user = userVO == null ? UserVO.builder().build() : userVO;
        List<EcoNewsCommentDto> ecoNewsCommentDtos = convertToEcoNewsCommentDtos(pages, user);

        return new PageableDto<>(
            ecoNewsCommentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long ecoNewsId, Long commentId, UserVO userVO) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(commentId)
            .filter(c -> c.getEcoNews().getId().equals(ecoNewsId))
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));

        if (userVO.getRole() != Role.ROLE_ADMIN && !userVO.getId().equals(comment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        if (comment.getComments() != null) {
            comment.getComments().forEach(c -> c.setStatus(CommentStatus.DELETED));
        }
        comment.setStatus(CommentStatus.DELETED);
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.COMMENT_OR_REPLY, AchievementAction.DELETE);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_COMMENT_OR_REPLY, userVO);
        ecoNewsCommentRepo.save(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void update(Long ecoNewsId, String text, Long id, UserVO userVO) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(id)
            .filter(c -> c.getEcoNews().getId().equals(ecoNewsId))
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (!userVO.getId().equals(comment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }
        comment.setStatus(CommentStatus.EDITED);
        comment.setText(text);
        ecoNewsCommentRepo.save(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(Long ecoNewsId, Long commentId, UserVO userVO) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(commentId)
            .filter(c -> c.getEcoNews().getId().equals(ecoNewsId))
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        EcoNewsCommentVO ecoNewsCommentVO = modelMapper.map(comment, EcoNewsCommentVO.class);
        if (comment.getUsersLiked().stream()
            .anyMatch(user -> user.getId().equals(userVO.getId()))) {
            ecoNewsService.unlikeComment(userVO, ecoNewsCommentVO);
            userNotificationService.removeActionUserFromNotification(modelMapper.map(comment.getUser(), UserVO.class),
                userVO, comment.getId(), NotificationType.ECONEWS_COMMENT_LIKE);
        } else {
            if (comment.getUser().getId().equals(userVO.getId())) {
                throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
            }
            ecoNewsService.likeComment(userVO, ecoNewsCommentVO);
            EcoNews ecoNews = comment.getEcoNews();
            userNotificationService.createNotification(modelMapper.map(comment.getUser(), UserVO.class), userVO,
                NotificationType.ECONEWS_COMMENT_LIKE, comment.getId(), comment.getText(),
                ecoNews.getId(), ecoNews.getTitle());
        }
        ecoNewsCommentRepo.save(modelMapper.map(ecoNewsCommentVO, EcoNewsComment.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void countLikes(AmountCommentLikesDto amountCommentLikesDto) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(amountCommentLikesDto.getId())
            .orElseThrow(() -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        boolean isLiked = comment.getUsersLiked().stream()
            .map(User::getId)
            .anyMatch(x -> x.equals(amountCommentLikesDto.getUserId()));
        amountCommentLikesDto.setLiked(isLiked);
        int size = comment.getUsersLiked().size();
        amountCommentLikesDto.setAmountLikes(size);
        messagingTemplate
            .convertAndSend("/topic/" + amountCommentLikesDto.getId() + "/comment", amountCommentLikesDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countReplies(Long ecoNewsId, Long commentId) {
        EcoNewsComment ecoNewsComment = ecoNewsCommentRepo.findById(commentId)
            .filter(c -> c.getEcoNews().getId().equals(ecoNewsId))
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        return ecoNewsCommentRepo.countByParentCommentId(ecoNewsComment.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countOfComments(Long ecoNewsId) {
        EcoNews ecoNews = ecoNewsRepo.findById(ecoNewsId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + ecoNewsId));
        return ecoNewsCommentRepo.countEcoNewsCommentByEcoNews(ecoNews.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<EcoNewsCommentDto> findAllComments(Pageable pageable, UserVO userVO, Long ecoNewsId,
        List<CommentStatus> statuses) {
        Page<EcoNewsComment> pages = ecoNewsCommentRepo
            .findAllByEcoNewsIdAndParentCommentIsNullAndStatusInOrderByCreatedDateDesc(pageable, ecoNewsId, statuses);
        UserVO user = userVO == null ? UserVO.builder().build() : userVO;
        List<EcoNewsCommentDto> ecoNewsCommentDtos = convertToEcoNewsCommentDtos(pages, user).stream()
            .map(this::setRepliesToComment)
            .toList();

        return new PageableDto<>(
            ecoNewsCommentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    private EcoNewsCommentDto setRepliesToComment(EcoNewsCommentDto comment) {
        comment.setReplies(ecoNewsCommentRepo.countByParentCommentId(comment.getId()));
        return comment;
    }

    private List<EcoNewsCommentDto> convertToEcoNewsCommentDtos(Page<EcoNewsComment> pages, UserVO user) {
        return pages.stream()
            .map(comment -> setCurrentUserLikedToComment(comment, user))
            .map(comment -> modelMapper.map(comment, EcoNewsCommentDto.class))
            .toList();
    }

    private EcoNewsComment setCurrentUserLikedToComment(EcoNewsComment comment, UserVO user) {
        comment.setCurrentUserLiked(comment.getUsersLiked().stream()
            .anyMatch(u -> u.getId().equals(user.getId())));
        return comment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void searchUsers(UserSearchDto searchUsers) {
        List<User> users = searchUsers.getSearchQuery() == null ? userRepo.findAll()
            : userRepo.searchUsers(searchUsers.getSearchQuery());

        List<UserTagDto> usersToTag = users.stream()
            .map(u -> modelMapper.map(u, UserTagDto.class))
            .limit(10)
            .collect(Collectors.toList());

        messagingTemplate.convertAndSend("/topic/" + searchUsers.getCurrentUserId() + "/searchUsers", usersToTag);
    }
}

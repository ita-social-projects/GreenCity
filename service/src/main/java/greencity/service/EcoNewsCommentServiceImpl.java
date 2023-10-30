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
import greencity.dto.friends.SearchFriendDto;
import greencity.dto.friends.TagFriendDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.AchievementCategoryType;
import greencity.enums.AchievementAction;
import greencity.enums.CommentStatus;
import greencity.enums.Role;
import greencity.enums.RatingCalculationEnum;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.rating.RatingCalculation;
import greencity.repository.EcoNewsCommentRepo;
import javax.servlet.http.HttpServletRequest;

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
    private final HttpServletRequest httpServletRequest;
    private final EcoNewsRepo ecoNewsRepo;
    private final UserRepo userRepo;

    /**
     * Method to save {@link greencity.entity.EcoNewsComment}.
     *
     * @param econewsId                   id of {@link EcoNews} to which we save
     *                                    comment.
     * @param addEcoNewsCommentDtoRequest dto with {@link EcoNewsComment} text,
     *                                    parentCommentId.
     * @param userVO                      {@link User} that saves the comment.
     * @return {@link AddEcoNewsCommentDtoResponse} instance.
     */

    @Override
    public AddEcoNewsCommentDtoResponse save(Long econewsId, AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest,
        UserVO userVO) {
        EcoNewsVO ecoNewsVO = ecoNewsService.findById(econewsId);
        EcoNewsComment ecoNewsComment = modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class);
        User user = modelMapper.map(userVO, User.class);
        ecoNewsComment.setUser(user);
        ecoNewsComment.setEcoNews(modelMapper.map(ecoNewsVO, EcoNews.class));
        if (addEcoNewsCommentDtoRequest.getParentCommentId() != 0) {
            EcoNewsComment parentComment =
                ecoNewsCommentRepo.findById(addEcoNewsCommentDtoRequest.getParentCommentId()).orElseThrow(
                    () -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
            if (parentComment.getParentComment() == null) {
                ecoNewsComment.setParentComment(parentComment);
            } else {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }
        }
        achievementCalculation
            .calculateAchievement(userVO,
                AchievementCategoryType.COMMENT_OR_REPLY, AchievementAction.ASSIGN);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.COMMENT_OR_REPLY, userVO);

        ecoNewsComment.setStatus(CommentStatus.ORIGINAL);

        return modelMapper.map(ecoNewsCommentRepo.save(ecoNewsComment), AddEcoNewsCommentDtoResponse.class);
    }

    /**
     * Method returns all comments to certain ecoNews specified by ecoNewsId.
     *
     * @param userVO    current {@link User}
     * @param ecoNewsId specifies {@link EcoNews} to which we search for comments
     * @return all comments to certain ecoNews specified by ecoNewsId.
     */
    @Override
    public PageableDto<EcoNewsCommentDto> findAllComments(Pageable pageable, UserVO userVO, Long ecoNewsId) {
        ecoNewsService.findById(ecoNewsId);
        Page<EcoNewsComment> pages = ecoNewsCommentRepo.findAllByParentCommentIsNullAndEcoNewsIdOrderByCreatedDateDesc(
            pageable, ecoNewsId);
        List<EcoNewsCommentDto> ecoNewsCommentDtos = pages
            .stream()
            .map(comment -> {
                comment.setCurrentUserLiked(comment.getUsersLiked().stream()
                    .anyMatch(u -> u.getId().equals(userVO.getId())));
                return comment;
            })
            .map(ecoNewsComment -> modelMapper.map(ecoNewsComment, EcoNewsCommentDto.class))
            .map(comment -> {
                comment.setReplies(ecoNewsCommentRepo.countByParentCommentId(comment.getId()));
                return comment;
            })
            .collect(Collectors.toList());

        return new PageableDto<>(
            ecoNewsCommentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * Method returns all replies to certain comment specified by parentCommentId.
     *
     * @param parentCommentId specifies {@link EcoNewsComment} to which we search
     *                        for replies
     * @param userVO          current {@link User}
     * @return all replies to certain comment specified by parentCommentId.
     */
    @Override
    public PageableDto<EcoNewsCommentDto> findAllReplies(Pageable pageable, Long parentCommentId, UserVO userVO) {
        Page<EcoNewsComment> pages = ecoNewsCommentRepo
            .findAllByParentCommentIdOrderByCreatedDateDesc(pageable, parentCommentId);
        if (pages.isEmpty()) {
            throw new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_PARENT_COMMENT_ID);
        }
        List<EcoNewsCommentDto> ecoNewsCommentDtos = pages
            .stream()
            .map(comment -> {
                comment.setCurrentUserLiked(comment.getUsersLiked().stream()
                    .anyMatch(u -> u.getId().equals(userVO.getId())));
                return comment;
            })
            .map(ecoNewsComment -> modelMapper.map(ecoNewsComment, EcoNewsCommentDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            ecoNewsCommentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * Method to mark {@link EcoNewsComment} specified by id as deleted.
     *
     * @param id     of {@link EcoNewsComment} to delete.
     * @param userVO current {@link User} that wants to delete.
     */
    @Override
    public void deleteById(Long id, UserVO userVO) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(id)
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
     * Method to change the existing {@link EcoNewsComment}.
     *
     * @param text   new text of {@link EcoNewsComment}.
     * @param id     to specify {@link EcoNewsComment} that user wants to change.
     * @param userVO current {@link User} that wants to change.
     */
    @Override
    @Transactional
    public void update(String text, Long id, UserVO userVO) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (!userVO.getId().equals(comment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }
        comment.setStatus(CommentStatus.EDITED);
        comment.setText(text);
        ecoNewsCommentRepo.save(comment);
    }

    /**
     * Method to like or dislike {@link EcoNewsComment} specified by id.
     *
     * @param id     of {@link EcoNewsComment} to like/dislike.
     * @param userVO current {@link User} that wants to like/dislike.
     */
    @Override
    public void like(Long id, UserVO userVO) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        EcoNewsCommentVO ecoNewsCommentVO = modelMapper.map(comment, EcoNewsCommentVO.class);
        if (comment.getUsersLiked().stream()
            .anyMatch(user -> user.getId().equals(userVO.getId()))) {
            ecoNewsService.unlikeComment(userVO, ecoNewsCommentVO);
        } else {
            ecoNewsService.likeComment(userVO, ecoNewsCommentVO);
        }
        ecoNewsCommentRepo.save(modelMapper.map(ecoNewsCommentVO, EcoNewsComment.class));
    }

    /**
     * Method returns count of likes to certain {@link EcoNewsComment} specified by
     * id.
     *
     * @param amountCommentLikesDto dto with id and count likes for comments.
     */
    @Override
    @Transactional
    public void countLikes(AmountCommentLikesDto amountCommentLikesDto) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(amountCommentLikesDto.getId()).orElseThrow(
            () -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        boolean isLiked = comment.getUsersLiked().stream().map(User::getId)
            .anyMatch(x -> x.equals(amountCommentLikesDto.getUserId()));
        amountCommentLikesDto.setLiked(isLiked);
        int size = comment.getUsersLiked().size();
        amountCommentLikesDto.setAmountLikes(size);
        messagingTemplate
            .convertAndSend("/topic/" + amountCommentLikesDto.getId() + "/comment", amountCommentLikesDto);
    }

    /**
     * Method to count replies to certain {@link EcoNewsComment}.
     *
     * @param id specifies parent comment to all replies
     * @return amount of replies
     */
    @Override
    public int countReplies(Long id) {
        if (ecoNewsCommentRepo.findById(id).isEmpty()) {
            throw new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION);
        }
        return ecoNewsCommentRepo.countByParentCommentId(id);
    }

    /**
     * Method to count not deleted comments to certain {@link EcoNews}.
     *
     * @param ecoNewsId to specify {@link EcoNews}
     * @return amount of comments
     */
    @Override
    public int countOfComments(Long ecoNewsId) {
        return ecoNewsCommentRepo.countEcoNewsCommentByEcoNews(ecoNewsRepo.findById(ecoNewsId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + ecoNewsId)));
    }

    /**
     * Method to get all active comments to {@link EcoNews} specified by ecoNewsId.
     *
     * @param pageable  page of news.
     * @param ecoNewsId specifies {@link EcoNews} to which we search for comments
     * @return all active comments to certain ecoNews specified by ecoNewsId.
     * @author Taras Dovganyuk
     */
    @Override
    public PageableDto<EcoNewsCommentDto> getAllActiveComments(Pageable pageable, UserVO userVO, Long ecoNewsId) {
        Page<EcoNewsComment> pages =
            ecoNewsCommentRepo
                .findAllByParentCommentIsNullAndEcoNewsIdAndStatusNotOrderByCreatedDateDesc(pageable, ecoNewsId,
                    CommentStatus.DELETED);
        UserVO user = userVO == null ? UserVO.builder().build() : userVO;
        List<EcoNewsCommentDto> ecoNewsCommentDtos = pages
            .stream()
            .map(comment -> {
                comment.setCurrentUserLiked(comment.getUsersLiked().stream()
                    .anyMatch(u -> u.getId().equals(user.getId())));
                return comment;
            })
            .map(ecoNewsComment -> modelMapper.map(ecoNewsComment, EcoNewsCommentDto.class))
            .map(comment -> {
                comment.setReplies(ecoNewsCommentRepo.countByParentCommentId(comment.getId()));
                return comment;
            })
            .collect(Collectors.toList());

        return new PageableDto<>(
            ecoNewsCommentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * Method returns all replies to certain comment specified by parentCommentId.
     *
     * @param parentCommentId specifies {@link EcoNewsComment} to which we search
     *                        for replies
     * @param userVO          current {@link User}
     * @return all replies to certain comment specified by parentCommentId.
     * @author Taras Dovganyuk
     */
    @Override
    public PageableDto<EcoNewsCommentDto> findAllActiveReplies(Pageable pageable, Long parentCommentId, UserVO userVO) {
        Page<EcoNewsComment> pages = ecoNewsCommentRepo
            .findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageable, parentCommentId,
                CommentStatus.DELETED);
        if (pages.isEmpty()) {
            throw new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_PARENT_COMMENT_ID);
        }
        UserVO user = userVO == null ? UserVO.builder().build() : userVO;
        List<EcoNewsCommentDto> ecoNewsCommentDtos = pages
            .stream()
            .map(comment -> {
                comment.setCurrentUserLiked(comment.getUsersLiked().stream()
                    .anyMatch(u -> u.getId().equals(user.getId())));
                return comment;
            })
            .map(ecoNewsComment -> modelMapper.map(ecoNewsComment, EcoNewsCommentDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            ecoNewsCommentDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }

    /**
     * Method that allow you to search Fiends by name.
     *
     * @param searchFriend dto with current user ID and search query
     *                     {@link SearchFriendDto}.
     *
     * @return list of {@link TagFriendDto} friends.
     * @author Anton Bondar
     */
    @Override
    @Transactional
    public List<TagFriendDto> searchFriends(SearchFriendDto searchFriend) {
        List<User> users = userRepo.searchFriends(searchFriend.getCurrentUserId(), searchFriend.getSearchQuery());
        return users
            .stream()
            .map(u -> modelMapper.map(u, TagFriendDto.class))
            .collect(Collectors.toList());
    }
}

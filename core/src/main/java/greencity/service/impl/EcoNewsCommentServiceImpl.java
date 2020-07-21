package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EcoNewsCommentRepo;
import greencity.service.EcoNewsCommentService;
import greencity.service.EcoNewsService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class EcoNewsCommentServiceImpl implements EcoNewsCommentService {
    private EcoNewsCommentRepo ecoNewsCommentRepo;
    private EcoNewsService ecoNewsService;
    private ModelMapper modelMapper;

    /**
     * Method to save {@link greencity.entity.EcoNewsComment}.
     *
     * @param econewsId                   id of {@link greencity.entity.EcoNews} to which we save comment.
     * @param addEcoNewsCommentDtoRequest dto with {@link greencity.entity.EcoNewsComment} text, parentCommentId.
     * @param user                        {@link User} that saves the comment.
     * @return {@link AddEcoNewsCommentDtoResponse} instance.
     */
    @Override
    public AddEcoNewsCommentDtoResponse save(Long econewsId, AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest,
                                             User user) {
        EcoNews ecoNews = ecoNewsService.findById(econewsId);
        EcoNewsComment ecoNewsComment = modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class);
        ecoNewsComment.setUser(user);
        ecoNewsComment.setEcoNews(ecoNews);
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

        return modelMapper.map(ecoNewsCommentRepo.save(ecoNewsComment), AddEcoNewsCommentDtoResponse.class);
    }

    /**
     * Method returns all comments to certain ecoNews specified by ecoNewsId.
     *
     * @param user      current {@link User}
     * @param ecoNewsId specifies {@link greencity.entity.EcoNews} to which we search for comments
     * @return all comments to certain ecoNews specified by ecoNewsId.
     */
    @Override
    public PageableDto<EcoNewsCommentDto> findAllComments(Pageable pageable, User user, Long ecoNewsId) {
        ecoNewsService.findById(ecoNewsId);
        Page<EcoNewsComment> pages = ecoNewsCommentRepo.findAllByParentCommentIsNullAndEcoNewsIdOrderByCreatedDateAsc(
            pageable, ecoNewsId);
        List<EcoNewsCommentDto> ecoNewsCommentDtos = pages
            .stream()
            .map(comment -> {
                comment.setCurrentUserLiked(comment.getUsersLiked().contains(user));
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
            pages.getPageable().getPageNumber()
        );
    }

    /**
     * Method returns all replies to certain comment specified by parentCommentId.
     *
     * @param parentCommentId specifies {@link greencity.entity.EcoNewsComment} to which we search for replies
     * @param user            current {@link User}
     * @return all replies to certain comment specified by parentCommentId.
     */
    @Override
    public PageableDto<EcoNewsCommentDto> findAllReplies(Pageable pageable, Long parentCommentId, User user) {
        Page<EcoNewsComment> pages = ecoNewsCommentRepo
                .findAllByParentCommentIdOrderByCreatedDateAsc(pageable, parentCommentId);
        List<EcoNewsCommentDto> ecoNewsCommentDtos = pages
                .stream()
                .map(comment -> {
                    comment.setCurrentUserLiked(comment.getUsersLiked().contains(user));
                    return comment;
                })
                .map(ecoNewsComment -> modelMapper.map(ecoNewsComment, EcoNewsCommentDto.class))
                .collect(Collectors.toList());

        return new PageableDto<>(
                ecoNewsCommentDtos,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber()
        );
    }

    /**
     * Method to mark {@link greencity.entity.EcoNewsComment} specified by id as deleted.
     *
     * @param id   of {@link greencity.entity.EcoNewsComment} to delete.
     * @param user current {@link User} that wants to delete.
     */
    @Override
    public void deleteById(Long id, User user) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));

        if (user.getRole() != ROLE.ROLE_ADMIN && user.getRole() != ROLE.ROLE_MODERATOR
            && !user.getId().equals(comment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        comment.setDeleted(true);
        ecoNewsCommentRepo.save(comment);
    }

    /**
     * Method to change the existing {@link greencity.entity.EcoNewsComment}.
     *
     * @param text new text of {@link greencity.entity.EcoNewsComment}.
     * @param id   to specify {@link greencity.entity.EcoNewsComment} that user wants to change.
     * @param user current {@link User} that wants to change.
     */
    @Override
    @Transactional
    public void update(String text, Long id, User user) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }
        comment.setText(text);
        ecoNewsCommentRepo.save(comment);
    }

    /**
     * Method to like or dislike {@link greencity.entity.EcoNewsComment} specified by id.
     *
     * @param id   of {@link greencity.entity.EcoNewsComment} to like/dislike.
     * @param user current {@link User} that wants to like/dislike.
     */
    public void like(Long id, User user) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (comment.getUsersLiked().contains(user)) {
            comment.getUsersLiked().remove(user);
        } else {
            comment.getUsersLiked().add(user);
        }
        ecoNewsCommentRepo.save(comment);
    }

    /**
     * Method returns count of likes to certain {@link greencity.entity.EcoNewsComment} specified by id.
     *
     * @param id of {@link greencity.entity.EcoNewsComment} to which we get count of likes.
     * @return count of likes to certain {@link greencity.entity.EcoNewsComment} specified by id.
     */
    @Override
    public int countLikes(Long id) {
        EcoNewsComment comment = ecoNewsCommentRepo.findById(id).orElseThrow(
            () -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION)
        );
        return comment.getUsersLiked().size();
    }

    /**
     * Method to count replies to certain {@link greencity.entity.EcoNewsComment}.
     *
     * @param id specifies parent comment to all replies
     * @return amount of replies
     */
    @Override
    public int countReplies(Long id) {
        if (!ecoNewsCommentRepo.findById(id).isPresent()) {
            throw new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION);
        }
        return ecoNewsCommentRepo.countByParentCommentId(id);
    }

    /**
     * Method to count not deleted comments to certain {@link greencity.entity.EcoNews}.
     *
     * @param ecoNewsId to specify {@link greencity.entity.EcoNews}
     * @return amount of comments
     */
    @Override
    public int countOfComments(Long ecoNewsId) {
        return ecoNewsCommentRepo.countOfComments(ecoNewsId);
    }
}

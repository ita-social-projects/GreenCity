package greencity.service;

import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.CommentAuthorDto;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.Habit;
import greencity.entity.User;
import greencity.enums.ArticleType;
import greencity.enums.CommentStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CommentRepo;
import greencity.repository.HabitRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final HabitRepo habitRepo;
    private final UserRepo userRepo;
    private final CommentRepo commentRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public AddCommentDtoResponse save(ArticleType articleType, Long articleId,
                                      AddCommentDtoRequest addCommentDtoRequest, UserVO userVO) {
        User articleAuthor = articleCheckIfExistsAndReturnAuthor(articleType, articleId);
        Comment comment = modelMapper.map(addCommentDtoRequest, Comment.class);
        comment.setArticleType(articleType);
        comment.setArticleId(articleId);
        comment.setUser(modelMapper.map(userVO, User.class));
        comment.setStatus(CommentStatus.ORIGINAL);
        System.out.println(articleAuthor);

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

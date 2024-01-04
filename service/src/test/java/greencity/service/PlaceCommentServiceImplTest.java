package greencity.service;

import greencity.ModelUtils;
import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentAdminDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.User;
import greencity.enums.UserStatus;
import greencity.rating.RatingCalculation;
import greencity.repository.PlaceCommentRepo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PlaceCommentServiceImplTest {
    @Mock
    private PlaceCommentRepo placeCommentRepo;
    @Mock
    private PlaceService placeService;
    @Mock
    private RestClient restClient;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private PlaceCommentServiceImpl placeCommentService;
    @Mock
    private UserService userService;
    @Mock
    private RatingCalculation ratingCalculation;
    @Mock
    private AchievementCalculation achievementCalculation;

    @Test
    void findByIdTest() {
        Comment comment = ModelUtils.getComment();
        CommentReturnDto commentReturnDto = ModelUtils.getCommentReturnDto();
        when(placeCommentRepo.findById(1L))
            .thenReturn(Optional.of(comment));
        when(modelMapper.map(comment, CommentReturnDto.class))
            .thenReturn(commentReturnDto);
        CommentReturnDto result = placeCommentService.findById(1L);
        assertEquals(1, (long) result.getId());
    }

    @Test
    void deleteByIdTest() {
        String accessToken = "Token";
        UserVO userVO = ModelUtils.getUserVO();
        Comment comment = ModelUtils.getComment();
        when(placeCommentRepo.findById(anyLong())).thenReturn(Optional.of(comment));
        doNothing().when(placeCommentRepo).delete(comment);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("email");
        when(restClient.findByEmail("email")).thenReturn(userVO);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(accessToken);

        when(userService.findById(any())).thenReturn(ModelUtils.getUserVO());

        placeCommentService.deleteById(1L);
        verify(placeCommentRepo, times(1)).delete(comment);
    }

    @Test
    void saveTest() {
        String token = "token";
        AddCommentDto addCommentDto = ModelUtils.getAddCommentDto();
        Comment comment = ModelUtils.getComment();
        when(placeService.findById(anyLong())).thenReturn(ModelUtils.getPlaceVO());
        UserVO userVO = ModelUtils.getUserVO();
        userVO.setUserStatus(UserStatus.ACTIVATED);
        when(restClient.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(addCommentDto, Comment.class)).thenReturn(comment);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        when(modelMapper.map(comment, CommentReturnDto.class))
            .thenReturn(ModelUtils.getCommentReturnDto());
        when(placeCommentRepo.save(any())).thenReturn(comment);
        placeCommentService.save(1L, addCommentDto, "email");
        verify(placeCommentRepo, times(1)).save(comment);
    }

    @Test
    void getAllCommentsTest() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<CommentAdminDto> commentAdminDtos = Collections.singletonList(new CommentAdminDto());
        List<Comment> list = Collections.singletonList(ModelUtils.getComment());
        Page<Comment> comments = new PageImpl<>(list, pageRequest, list.size());

        PageableDto<CommentAdminDto> result = new PageableDto<>(commentAdminDtos, commentAdminDtos.size(), 0, 1);
        when(modelMapper.map(list.getFirst(), CommentAdminDto.class)).thenReturn(new CommentAdminDto());
        when(placeCommentRepo.findAll(pageRequest)).thenReturn(comments);

        assertEquals(result, placeCommentService.getAllComments(pageRequest));
    }
}

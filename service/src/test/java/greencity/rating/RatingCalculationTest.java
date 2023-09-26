package greencity.rating;

import greencity.ModelUtils;
import greencity.enums.RatingCalculationEnum;
import greencity.client.RestClient;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.user.UserVO;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import greencity.service.RatingStatisticsService;
import java.time.ZonedDateTime;

import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingCalculationTest {
    @Mock
    private RestClient restClient;
    @Mock
    private RatingStatisticsService ratingStatisticsService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RatingCalculation ratingCalculation;
    @Mock
    private UserService userService;

    @Test
    void ratingCalculation() {

        RatingCalculationEnum rating = RatingCalculationEnum.COMMENT_OR_REPLY;
        User user = ModelUtils.getUser();
        user.setRating(1D);
        UserVO userVO = ModelUtils.getUserVO();
        userVO.setRating(1D);
        ZonedDateTime now = ZonedDateTime.now();
        RatingStatistics ratingStatistics = RatingStatistics
            .builder()
            .rating(userVO.getRating() + RatingCalculationEnum.COMMENT_OR_REPLY.getRatingPoints())
            .ratingCalculationEnum(rating)
            .user(user)
            .pointsChanged(rating.getRatingPoints())
            .build();

        RatingStatisticsVO ratingStatisticsVO = RatingStatisticsVO.builder()
            .id(1L)
            .rating(userVO.getRating())
            .ratingCalculationEnum(RatingCalculationEnum.COMMENT_OR_REPLY)
            .user(userVO)
            .createDate(now)
            .pointsChanged(rating.getRatingPoints())
            .build();
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(ratingStatistics, RatingStatisticsVO.class)).thenReturn(ratingStatisticsVO);
        when(ratingStatisticsService.save(ratingStatisticsVO)).thenReturn(ratingStatisticsVO);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.COMMENT_OR_REPLY, userVO);
        verify(ratingStatisticsService).save(ratingStatisticsVO);

    }
}
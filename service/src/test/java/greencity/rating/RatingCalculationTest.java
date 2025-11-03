package greencity.rating;

import greencity.ModelUtils;
import greencity.dto.ratingstatistics.RatingPointsDto;
import greencity.entity.RatingPoints;
import greencity.repository.RatingPointsRepo;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.service.RatingStatisticsService;

import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingCalculationTest {
    @Mock
    private RatingStatisticsService ratingStatisticsService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RatingCalculation ratingCalculation;
    @Mock
    private UserService userService;
    @Mock
    private RatingPointsRepo ratingPointsRepo;

    @Test
    void ratingCalculation() {
        RatingPoints rating = RatingPoints.builder()
            .id(1L)
            .name("COMMENT_OR_REPLY")
            .points(5)
            .build();
        User user = ModelUtils.getUser();

        UserVO userVO = ModelUtils.getUserVO();
        user.setRating(1D);

        RatingStatisticsVO ratingStatisticsVO = RatingStatisticsVO.builder()
            .rating(user.getRating())
            .ratingPoints(RatingPointsDto.builder()
                .id(rating.getId())
                .name(rating.getName())
                .points(rating.getPoints())
                .build())
            .user(userVO)
            .pointsChanged(rating.getPoints())
            .build();

        when(modelMapper.map(rating, RatingPointsDto.class)).thenReturn(ratingStatisticsVO.getRatingPoints());
        when(userService.findUserRating(userVO.getId())).thenReturn(user.getRating());

        ratingCalculation.ratingCalculation(rating, userVO);

        verify(modelMapper).map(rating, RatingPointsDto.class);
        verify(userService).updateUserRating(1L, 6.0d);
        verify(ratingStatisticsService).save(ratingStatisticsVO);
    }
}
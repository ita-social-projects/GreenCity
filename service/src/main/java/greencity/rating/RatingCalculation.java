package greencity.rating;

import greencity.dto.ratingstatistics.RatingPointsDto;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.user.UserVO;
import greencity.entity.RatingPoints;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import greencity.service.RatingStatisticsService;
import greencity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingCalculation {
    private final RatingStatisticsService ratingStatisticsService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    /**
     * Method that calculates the user rating.
     *
     * @param rating of {@link RatingPoints}
     * @param userVo of {@link UserVO}
     */
    public void ratingCalculation(RatingPoints rating, UserVO userVo) {
        Double userRating = userService.findUserRating(userVo.getId());
        double newRating = userRating + rating.getPoints();
        userService.updateUserRating(userVo.getId(), newRating);

        RatingStatisticsVO ratingStatisticsVO = RatingStatisticsVO
            .builder()
            .rating(userRating)
            .ratingPoints(modelMapper.map(rating, RatingPointsDto.class))
            .user(userVo)
            .pointsChanged(rating.getPoints())
            .build();

        ratingStatisticsService.save(ratingStatisticsVO);
    }
}

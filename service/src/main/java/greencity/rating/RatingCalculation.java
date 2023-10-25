package greencity.rating;

import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.user.UserVO;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import greencity.enums.RatingCalculationEnum;
import greencity.service.RatingStatisticsService;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RatingCalculation {
    private RatingStatisticsService ratingStatisticsService;
    private final ModelMapper modelMapper;
    private UserService userService;

    /**
     * Method that calculates the user rating.
     *
     * @param rating of {@link RatingCalculationEnum}
     * @param userVo of {@link UserVO}
     */
    public void ratingCalculation(RatingCalculationEnum rating, UserVO userVo) {
        User user = modelMapper.map(userVo, User.class);
        double newRating = userVo.getRating() + rating.getRatingPoints();
        userVo.setRating(newRating > 0 ? newRating : 0);
        userService.updateUserRating(user.getId(), userVo.getRating());
        RatingStatistics ratingStatistics = RatingStatistics
            .builder()
            .rating(userVo.getRating())
            .ratingCalculationEnum(rating)
            .user(user)
            .pointsChanged(rating.getRatingPoints())
            .build();
        ratingStatisticsService.save(modelMapper.map(ratingStatistics, RatingStatisticsVO.class));
    }
}

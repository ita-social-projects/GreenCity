package greencity.rating;

import greencity.client.RestClient;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.user.UserVO;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import greencity.enums.RatingCalculationEnum;
import greencity.service.RatingStatisticsService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static greencity.constant.AppConstant.AUTHORIZATION;

@Component
@AllArgsConstructor
public class RatingCalculation {
    private RestClient restClient;
    private RatingStatisticsService ratingStatisticsService;
    private final ModelMapper modelMapper;
    private final HttpServletRequest httpServletRequest;

    /**
     * Method that calculates the user rating.
     * 
     * @param rating of {@link RatingCalculationEnum}
     * @param userVo of {@link UserVO}
     */
    public void ratingCalculation(RatingCalculationEnum rating, UserVO userVo) {
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        User user = modelMapper.map(userVo, User.class);
        userVo.setRating(userVo.getRating() + rating.getRatingPoints());
        restClient.save(userVo, accessToken);
        RatingStatistics ratingStatistics = RatingStatistics
            .builder()
            .rating(userVo.getRating())
            .ratingCalculationEnum(rating)
            .user(user)
            .pointsChanged(rating.getRatingPoints())
            .build();
        ratingStatisticsService.save(modelMapper.map(ratingStatistics, RatingStatisticsVO.class));
    }
    /**
     * Method that calculates the user rating.
     *
     * @param rating of {@link RatingCalculationEnum}
     * @param userVo of {@link UserVO}
     */
    public void ratingCalculation(RatingCalculationEnum rating, int points, UserVO userVo) {
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        User user = modelMapper.map(userVo, User.class);
        userVo.setRating(userVo.getRating() +points);
        restClient.save(userVo, accessToken);
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

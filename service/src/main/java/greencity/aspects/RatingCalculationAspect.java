package greencity.aspects;

import greencity.annotations.RatingCalculation;
import greencity.annotations.RatingCalculationEnum;
import greencity.client.RestClient;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.user.UserVO;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import greencity.service.RatingStatisticsService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * This aspect is used for User rating calculating.
 *
 * @author Taras Dovganyuk
 */
@Slf4j(topic = "ratingLog")
@Aspect
@Component
@AllArgsConstructor
@Builder
public class RatingCalculationAspect {
    private RestClient restClient;
    private RatingStatisticsService ratingStatisticsService;
    private final ModelMapper modelMapper;

    /**
     * This pointcut {@link Pointcut} is used for define annotation to processing.
     *
     * @param ratingCalculation is used for recognize methods to processing.
     */
    @Pointcut("@annotation(ratingCalculation)")
    public void myAnnotationPointcut(RatingCalculation ratingCalculation) {
        /*
         * Complete if needed.
         */
    }

    /**
     * This method is used for User {@link User} rating calculation and logging.
     *
     * @param ratingCalculation is used for recognize methods to processing.
     * @author Taras Dovganyuk
     */
    @AfterReturning(pointcut = "myAnnotationPointcut(ratingCalculation)",
        argNames = "ratingCalculation")
    private void ratingCalculation(RatingCalculation ratingCalculation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = modelMapper.map(restClient.findByEmail(authentication.getName()), User.class);
        RatingCalculationEnum rating = ratingCalculation.rating();
        user.setRating(user.getRating() + rating.getRatingPoints());
        /* restClient.save(modelMapper.map(user, UserVO.class), accessToken); */
        RatingStatistics ratingStatistics = RatingStatistics
            .builder()
            .rating(user.getRating())
            .ratingCalculationEnum(rating)
            .user(user)
            .pointsChanged(rating.getRatingPoints())
            .build();
        ratingStatisticsService.save(modelMapper.map(ratingStatistics, RatingStatisticsVO.class));

        log.info("Event {}, UserId {}, points {}, current rating {}",
            rating, user.getId(), rating.getRatingPoints(), user.getRating());
    }
}

package greencity.aspects;

import greencity.ModelUtils;
import greencity.annotations.RatingCalculation;
import greencity.annotations.RatingCalculationEnum;
import greencity.client.RestClient;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.user.UserVO;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import greencity.service.RatingStatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.annotation.Annotation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingCalculationAspectTest {
    @InjectMocks
    RatingCalculationAspect ratingCalculationAspect;
//    @Mock
//    private UserService userService;
    @Mock
    private RatingStatisticsService ratingStatisticsService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private RestClient restClient;
    @Mock
    RatingCalculationAspect ratingCalculationAspectMock;

    @Test
    void myAnnotationPointcut() {
        ratingCalculationAspect = RatingCalculationAspect.builder()
//            .userService(userService)
            .modelMapper(modelMapper)
            .restClient(restClient)
            .ratingStatisticsService(ratingStatisticsService)
            .build();
        class Test implements RatingCalculation {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public RatingCalculationEnum rating() {
                RatingCalculationEnum ratingCalculationEnum = RatingCalculationEnum.ADD_ECO_NEWS;
                return ratingCalculationEnum;
            }
        }
        Test test = new Test();
        User user = ModelUtils.getUser();
        user.setRating(20.0 + test.rating().getRatingPoints());
        UserVO userVO = ModelUtils.getUserVO();
        RatingStatistics ratingStatistics = RatingStatistics.builder()
            .rating(60.0)
            .ratingCalculationEnum(test.rating())
            .user(user)
            .pointsChanged(test.rating().getRatingPoints())
            .build();
        RatingStatisticsVO ratingStatisticsVO = RatingStatisticsVO.builder()
            .id(ratingStatistics.getId())
            .rating(ratingStatistics.getRating())
            .ratingCalculationEnum(ratingStatistics.getRatingCalculationEnum())
            .pointsChanged(ratingStatistics.getPointsChanged())
            .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("email");
        when(restClient.findByEmail("email")).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
//        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
//        when(userService.save(userVO)).thenReturn(userVO);
        when(modelMapper.map(ratingStatistics, RatingStatisticsVO.class)).thenReturn(ratingStatisticsVO);
        when(ratingStatisticsService.save(ratingStatisticsVO)).thenReturn(ratingStatisticsVO);
        ratingCalculationAspectMock.myAnnotationPointcut(test);
        verify(ratingCalculationAspectMock).myAnnotationPointcut(test);
        ratingCalculationAspect.myAnnotationPointcut(test);
        ReflectionTestUtils.invokeMethod(ratingCalculationAspect, "ratingCalculation", test);

    }
}

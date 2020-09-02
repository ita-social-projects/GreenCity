package greencity.aspects;

import greencity.annotations.RatingCalculation;
import greencity.constant.ErrorMessage;
import greencity.constant.RatingConstants;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.EcoNewsComment;
import greencity.entity.TipsAndTricksComment;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EcoNewsCommentRepo;
import greencity.repository.PlaceCommentRepo;
import greencity.repository.TipsAndTricksCommentRepo;
import greencity.repository.UserRepo;
import greencity.service.UserService;
import greencity.service.impl.EcoNewsCommentServiceImpl;
import greencity.service.impl.PlaceCommentServiceImpl;
import greencity.service.impl.TipsAndTricksCommentServiceImpl;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * This aspect is used for User rating calculating.
 *
 * @author Taras Dovganyuk
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class RatingCalculationAspect {
    private UserService userService;
    private UserRepo userRepo;
    private TipsAndTricksCommentRepo tipsAndTricksCommentRepo;
    private EcoNewsCommentRepo ecoNewsCommentRepo;
    private PlaceCommentRepo placeCommentRepo;

    /**
     * This method is used for retrieve User {@link User} from method arguments.
     *
     * @param joinPoint is used for annotated parameter observation.
     * @author Taras Dovganyuk
     */
    private static User getUserFromMethodArguments(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        return (User) Arrays.stream(joinPoint.getArgs())
            .filter(x -> x instanceof User)
            .findFirst()
            .orElseThrow(
                () -> new NotFoundException("Argument of type 'User' not found in method " + methodName));
    }

    /**
     * This method is used for retrieve id of type Long from method arguments.
     *
     * @param joinPoint is used for annotated parameter observation.
     * @author Taras Dovganyuk
     */
    private static Long getCommentIdFromMethodArguments(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        return (Long) Arrays.stream(joinPoint.getArgs())
            .filter(x -> x.getClass().equals(Long.class))
            .findFirst()
            .orElseThrow(
                () -> new NotFoundException("Argument of type 'Long' not found in method " + methodName));
    }

    /**
     * This method is used for changing User's {@link User} rating.
     *
     * @param user   {@link User} whose rating is changing.
     * @param rating amount of points for rating change.
     * @author Taras Dovganyuk
     */
    private static void userRatingModification(User user, Float rating) {
        user.setRating(user.getRating() + rating);
    }

    /**
     * This pointcut {@link Pointcut} is used for define annotation to processing.
     *
     * @param ratingCalculation is used for recognize methods to processing.
     */
    @Pointcut("@annotation(ratingCalculation)")
    public void myAnnotationPointcut(RatingCalculation ratingCalculation) {
    }

    /**
     * This method is used for User {@link User} rating calculation.
     *
     * @param body              that is used for annotated parameter observation.
     * @param ratingCalculation is used for recognize methods to processing.
     * @author Taras Dovganyuk
     */
    @AfterReturning(pointcut = "myAnnotationPointcut(ratingCalculation)", returning = "body",
        argNames = "ratingCalculation,body")
    private void addTipsAndTricksRatingCalculation(RatingCalculation ratingCalculation,
                                                   Object body) {
        if (body instanceof TipsAndTricksDtoResponse) {
            TipsAndTricksDtoResponse tipsAndTricksDtoResponse = (TipsAndTricksDtoResponse) body;
            Long userId = tipsAndTricksDtoResponse.getAuthor().getId();
            User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
            userRatingModification(user, RatingConstants.ADD_TIPS_AND_TRICKS_RATING);
            userService.save(user);
        }
    }

    /**
     * This method is used for User {@link User} rating calculation.
     *
     * @param body              that is used for annotated parameter observation.
     * @param ratingCalculation is used for recognize methods to processing.
     * @author Taras Dovganyuk
     */
    @AfterReturning(pointcut = "myAnnotationPointcut(ratingCalculation)", returning = "body",
        argNames = "ratingCalculation,body")
    private void createEcoNewsRatingCalculation(RatingCalculation ratingCalculation, Object body) {
        if (body instanceof AddEcoNewsDtoResponse) {
            AddEcoNewsDtoResponse addEcoNewsDtoResponse = (AddEcoNewsDtoResponse) body;
            Long userId = addEcoNewsDtoResponse.getEcoNewsAuthorDto().getId();
            User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
            userRatingModification(user, RatingConstants.CREATE_ECO_NEWS_RATING);
            userService.save(user);
        }
    }

    /**
     * This method is used for User {@link User} rating calculation.
     *
     * @param body              that is used for annotated parameter observation.
     * @param ratingCalculation is used for recognize methods to processing.
     * @author Taras Dovganyuk
     */
    @AfterReturning(pointcut = "myAnnotationPointcut(ratingCalculation)", returning = "body",
        argNames = "joinPoint,ratingCalculation,body")
    private void tipsAndTricksCommentRatingCalculation(JoinPoint joinPoint, RatingCalculation ratingCalculation,
                                                       Object body) {
        User user = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();

        if (joinPoint.getTarget() instanceof TipsAndTricksCommentServiceImpl) {
            if (methodName.equals("save")) {
                user = getUserFromMethodArguments(joinPoint);
                userRatingModification(user, RatingConstants.ADD_COMMENT_OR_REPLY_RATING);
            }
            if (methodName.equals("deleteById")) {
                user = getUserFromMethodArguments(joinPoint);
                userRatingModification(user, RatingConstants.DELETE_COMMENT_OR_REPLY_RATING);
            }
            if (methodName.equals("like")) {
                Long id = getCommentIdFromMethodArguments(joinPoint);
                TipsAndTricksComment comment = tipsAndTricksCommentRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
                user = getUserFromMethodArguments(joinPoint);
                if (comment.getUsersLiked().contains(user)) {
                    userRatingModification(user, RatingConstants.LIKE_COMMENT_OR_REPLY_RATING);
                } else {
                    userRatingModification(user, RatingConstants.UNLIKE_COMMENT_OR_REPLY_RATING);
                }
            }
            userService.save(user);
        }
    }

    /**
     * This method is used for User {@link User} rating calculation.
     *
     * @param body              that is used for annotated parameter observation.
     * @param ratingCalculation is used for recognize methods to processing.
     * @author Taras Dovganyuk
     */
    @AfterReturning(pointcut = "myAnnotationPointcut(ratingCalculation)", returning = "body",
        argNames = "joinPoint,ratingCalculation,body")
    private void placeCommentRatingCalculation(JoinPoint joinPoint, RatingCalculation ratingCalculation, Object body) {
        User user = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();

        if (joinPoint.getTarget() instanceof PlaceCommentServiceImpl) {
            if (methodName.equals("save")) {
                String userEmail = (String) Arrays.stream(joinPoint.getArgs())
                    .filter(x -> x instanceof String)
                    .findFirst()
                    .orElseThrow(
                        () -> new NotFoundException("Argument of type 'String' not found in method " + methodName));
                user = userRepo.findByEmail(userEmail)
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + userEmail));
                userRatingModification(user, RatingConstants.ADD_COMMENT_OR_REPLY_RATING);
            }
            if (methodName.equals("deleteById")) {
                user = getUserFromMethodArguments(joinPoint);
                userRatingModification(user, RatingConstants.DELETE_COMMENT_OR_REPLY_RATING);
            }
            userService.save(user);
        }
    }

    /**
     * This method is used for User {@link User} rating calculation.
     *
     * @param body              that is used for annotated parameter observation.
     * @param ratingCalculation is used for recognize methods to processing.
     * @author Taras Dovganyuk
     */
    @AfterReturning(pointcut = "myAnnotationPointcut(ratingCalculation)",
        returning = "body", argNames = "joinPoint,ratingCalculation,body")
    private void ecoNewsCommentRatingCalculation(JoinPoint joinPoint, RatingCalculation ratingCalculation,
                                                 Object body) {
        User user = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();

        if (joinPoint.getTarget() instanceof EcoNewsCommentServiceImpl) {
            if (methodName.equals("save")) {
                user = getUserFromMethodArguments(joinPoint);
                userRatingModification(user, RatingConstants.ADD_COMMENT_OR_REPLY_RATING);
            }
            if (methodName.equals("deleteById")) {
                user = getUserFromMethodArguments(joinPoint);
                userRatingModification(user, RatingConstants.DELETE_COMMENT_OR_REPLY_RATING);
            }
            if (methodName.equals("like")) {
                Long id = getCommentIdFromMethodArguments(joinPoint);
                EcoNewsComment comment = ecoNewsCommentRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
                user = getUserFromMethodArguments(joinPoint);
                if (comment.getUsersLiked().contains(user)) {
                    userRatingModification(user, RatingConstants.LIKE_COMMENT_OR_REPLY_RATING);
                } else {
                    userRatingModification(user, RatingConstants.UNLIKE_COMMENT_OR_REPLY_RATING);
                }
            }
            userService.save(user);
        }
    }
}

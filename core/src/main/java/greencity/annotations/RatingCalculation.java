package greencity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used for User's rating calculation.
 *
 * @author Dovganyuk Taras
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RatingCalculation {
    /**
     * This method is used for retrieve rating points from annotated methods.
     *
     * @author Dovganyuk Taras
     */
    RatingCalculationEnum rating();
}

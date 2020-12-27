package greencity.annotations;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used for choosing language code as a query parameter for
 * example: "en", "ua", "ru".
 *
 * @author Yurii Savchenko
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
    @ApiImplicitParam(name = "lang", value = "Code of the needed language.", dataType = "string", paramType = "query")})
public @interface ApiLocale {
}

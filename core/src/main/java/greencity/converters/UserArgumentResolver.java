package greencity.converters;

import greencity.annotations.CurrentUser;
import greencity.constant.ErrorMessage;
import greencity.entity.User;
import greencity.exception.exceptions.WrongEmailException;
import greencity.service.UserService;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@AllArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    UserService userService;

    /**
     * Method checks if parameter is {@link User} and is annotated with {@link CurrentUser}.
     * @param parameter method parameter
     * @return boolean
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null
            && parameter.getParameterType().equals(User.class);
    }

    /**
     * Method returns {@link User} by principal.
     * @return {@link User}
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Principal principal = webRequest.getUserPrincipal();
        return Optional.ofNullable(userService.findByEmail(Objects.requireNonNull(principal).getName()))
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + principal.getName())
            );
    }
}

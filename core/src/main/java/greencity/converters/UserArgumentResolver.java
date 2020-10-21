package greencity.converters;

import greencity.annotations.CurrentUser;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.service.UserService;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
    ModelMapper modelMapper;

    /**
     * Method checks if parameter is {@link User} and is annotated with {@link CurrentUser}.
     *
     * @param parameter method parameter
     * @return boolean
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null
            && parameter.getParameterType().equals(User.class) || parameter.getParameterType().equals(UserVO.class);
    }

    /**
     * Method returns {@link User} by principal.
     *
     * @return {@link User}
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Principal principal = webRequest.getUserPrincipal();
        if ((parameter.getParameterType().equals(UserVO.class))){
            return principal != null ? modelMapper.map(userService.findByEmail(principal.getName()), UserVO.class): null;
        }
        return principal != null ? userService.findByEmail(principal.getName()) : null;
    }
}

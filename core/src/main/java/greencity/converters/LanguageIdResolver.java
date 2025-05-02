package greencity.converters;

import greencity.annotations.LanguageId;
import greencity.client.UserRemoteClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LanguageIdResolver implements HandlerMethodArgumentResolver {

    private final UserRemoteClient userRemoteClient;

    /**
     * Method checks if parameter is {@link java.util.Locale} and is annotated with
     * {@link greencity.annotations.LanguageId}.
     *
     * @param parameter method parameter
     * @return boolean
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LanguageId.class) != null &&
                Long.class.isAssignableFrom(parameter.getParameterType());
    }

    /**
     * Method returns {@link Long} language id by language code.
     *
     * @return {@link Long} language id
     */
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        String languageCode = webRequest.getParameter("lang");
        return userRemoteClient.findLanguageIdByCode(languageCode);
    }
}

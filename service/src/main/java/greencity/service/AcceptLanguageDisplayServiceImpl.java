//package greencity.service;
//
//import static greencity.constant.LanguageServiceConstants.*;
//import greencity.enums.Language;
//import static greencity.utils.LanguageResolverUtil.parseLanguage;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
///**
// * Service implementation for determining the user's preferred language based on the `Accept-Language` HTTP header.
// *
// * <p>This service uses the {@link greencity.utils.LanguageResolverUtil} to parse the language
// * and map it to a supported language defined in {@link greencity.enums.Language}.</p>
// *
// * <p>If the `Accept-Language` header is missing or invalid, the service defaults to English.</p>
// */
//@Service
//public class AcceptLanguageDisplayServiceImpl implements AcceptLanguageDisplayService {
//    /**
//     * Resolves the user's preferred language based on the `Accept-Language` HTTP header.
//     *
//     * @return the display name of the resolved language, as defined in {@link greencity.enums.Language}.
//     *         Defaults to English if the header is missing or invalid.
//     */
//    @Override
//    public String resolveLanguage() {
//        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
//        if (!(attributes instanceof ServletRequestAttributes)) {
//            return Language.ENGLISH.getDisplayName();
//        }
//        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
//        return parseLanguage(request.getHeader(ACCEPT_LANGUAGE_HEADER));
//    }
//}
//

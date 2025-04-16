package greencity.service;

import static greencity.constant.LanguageServiceConstants.*;
import greencity.enums.Language;
import static greencity.utils.LanguageResolverUtil.parseLanguage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AcceptLanguageDisplayServiceImpl implements AcceptLanguageDisplayService {
    @Override
    public String resolveLanguage() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) {
            return Language.ENGLISH.getDisplayName();
        }

        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        return parseLanguage(request.getHeader(ACCEPT_LANGUAGE_HEADER));
    }
}


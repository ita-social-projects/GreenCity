package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.service.LanguageService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LanguageServiceImpl implements LanguageService {
    private HttpServletRequest request;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public LanguageServiceImpl(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String extractLanguageCodeFromRequest() {
        String languageCode = request.getParameter("language");

        if (languageCode == null) {
            return AppConstant.DEFAULT_LANGUAGE_CODE;
        }

        return languageCode;
    }
}

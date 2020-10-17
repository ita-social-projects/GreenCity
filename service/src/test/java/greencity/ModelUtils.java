package greencity;

import greencity.entity.Language;
import greencity.service.constant.AppConstant;
import java.util.Collections;

public class ModelUtils {
    public static Language getLanguage() {
        return new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }
}

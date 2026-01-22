package greencity.config.dotenv;

import greencity.constant.AppConstant;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DotEnvConditionChecker {
    private static Boolean cachedValue = null;

    public static boolean isEnabled() {
        if (cachedValue == null) {
            try {
                cachedValue = Files
                    .exists(Paths.get(System.getProperty("user.dir") + File.separator + AppConstant.DOTENV_FILENAME));
            } catch (SecurityException e) {
                cachedValue = false;
            }
        }
        return cachedValue;
    }
}

package greencity.config.dotenv;

import greencity.constant.AppConstant;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DotEnvConditionChecker {
    public static boolean isEnabled() {
        return Files.exists(Paths.get(System.getProperty("user.dir") + File.separator + AppConstant.DOTENV_FILENAME));
    }
}

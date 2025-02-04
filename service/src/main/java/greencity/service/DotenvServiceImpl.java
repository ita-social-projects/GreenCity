package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.BadSecretKeyException;
import greencity.exception.exceptions.FunctionalityNotAvailableException;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
@Lazy // TODO: check if it is necessary
@RequiredArgsConstructor
public class DotenvServiceImpl implements DotenvService {
    private Dotenv dotenv;
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateSecretKey(String secretKey) {
        loadEnvFile();
        String actualKey = dotenv.get("logs.secretKey");

        if (actualKey == null || !passwordEncoder.matches(secretKey, actualKey)) {
            throw new BadSecretKeyException(ErrorMessage.BAD_SECRET_KEY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDotenvFile(String secretKey) {
        validateSecretKey(secretKey);

        String dotenvFilePath = System.getProperty("user.dir") + File.separator + ".env";
        File dotenvFile = new File(dotenvFilePath);

        if (!dotenvFile.exists()) {
            throw new FunctionalityNotAvailableException(ErrorMessage.DOTENV_DELETED_OR_NOT_FOUND);
        }

        if (!dotenvFile.delete()) {
            throw new FunctionalityNotAvailableException(ErrorMessage.CANNOT_DELETE_DOTENV);
        }
    }

    /**
     * Loads the environment variables from the `.env` file.
     * If the `.env` file is missing or cannot be loaded, this method throws a
     * {@link FunctionalityNotAvailableException} to indicate that the required
     * functionality is unavailable.
     *
     *
     * @throws FunctionalityNotAvailableException if the `.env` file cannot be
     *                                            loaded
     * @author Hrenevych Ivan
     */
    private void loadEnvFile() {
        try {
            dotenv = Dotenv.load();
        } catch (DotenvException ex) {
            throw new FunctionalityNotAvailableException(ErrorMessage.FUNCTIONALITY_NOT_AVAILABLE);
        }
    }
}

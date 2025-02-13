package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.BadSecretKeyException;
import greencity.exception.exceptions.FunctionalityNotAvailableException;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
@Profile({"dev", "test"})
public class DotenvServiceImpl implements DotenvService {
    //TODO: create a bean of Dotenv to mock it in tests
    private Dotenv dotenv;
    private final PasswordEncoder passwordEncoder;

    private static final String DOTENV_FILENAME = "secretKeys.env";

    public DotenvServiceImpl(@Qualifier("DotenvPasswordEncoder") PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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

        String dotenvFilePath = System.getProperty("user.dir") + File.separator + DOTENV_FILENAME;
        File dotenvFile = new File(dotenvFilePath);

        if (!dotenvFile.exists()) {
            throw new FunctionalityNotAvailableException(ErrorMessage.DOTENV_DELETED_OR_NOT_FOUND);
        }

        if (!dotenvFile.delete()) {
            throw new FunctionalityNotAvailableException(ErrorMessage.CANNOT_DELETE_DOTENV);
        }
    }

    /**
     * Loads the environment variables from the `.env` file. If the `.env` file is
     * missing or cannot be loaded, this method throws a
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
            dotenv = Dotenv.configure()
                    .filename(DOTENV_FILENAME)
                    .load();
        } catch (DotenvException ex) {
            throw new FunctionalityNotAvailableException(ErrorMessage.FUNCTIONALITY_NOT_AVAILABLE);
        }
    }
}

package greencity.validator;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.InvalidURLException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlValidator {
    /**
     * Method that checks if received URL is valid (string could be parsed as a URI reference and URL is well formed).
     */
    public static boolean isUrlValid(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException e) {
            throw new InvalidURLException(ErrorMessage.MALFORMED_URL);
        } catch (URISyntaxException e) {
            throw new InvalidURLException(ErrorMessage.INVALID_URI);
        }
    }
}

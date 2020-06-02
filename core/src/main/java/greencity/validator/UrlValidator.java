package greencity.validator;

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
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}

package greencity.validator;

import static greencity.ModelUtils.getUrl;
import greencity.exception.exceptions.InvalidURLException;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class UrlValidatorTest {

    @Test
    void UrlValidatorTrueTest() throws MalformedURLException {
        String url = getUrl().toString();
        assertTrue(UrlValidator.isUrlValid(url));
    }

    @Test
    void UrlValidatorMalformedURLExceptionTest() {
        String url = "ht://www.tutorialspoint.com/";
        Assertions.assertThrowsExactly(InvalidURLException.class, () -> UrlValidator.isUrlValid(url),
            "Malformed URL. The string could not be parsed.");
    }

    @Test
    void UrlValidatorURISyntaxExceptionTest() {
        String url = "https:// .";
        Assertions.assertThrows(InvalidURLException.class, () -> UrlValidator.isUrlValid(url),
            "The string could not be parsed as a URI reference.");
    }

    @Test
    void IllegalArgumentExceptionTest() {
        String url2 = "";
        Assertions.assertThrows(InvalidURLException.class, () -> UrlValidator.isUrlValid(url2),
            "The string could not be parsed as a URI reference.");
    }
}

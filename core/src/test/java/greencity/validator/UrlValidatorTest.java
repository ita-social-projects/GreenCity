package greencity.validator;

import greencity.exception.exceptions.InvalidURLException;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getUrl;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class UrlValidatorTest {

    @Test
    void UrlValidatorTrueTest() throws MalformedURLException {
        String url = getUrl().toString();
        assertTrue(UrlValidator.isUrlValid(url));
    }

    @Test
    void UrlValidatorMalformedURLExceptionTest() {
        String url = "ttt://";
        Assertions.assertThrows(InvalidURLException.class, () -> UrlValidator.isUrlValid(url));
    }

    @Test
    void UrlValidatorURISyntaxExceptionTest() {
        String url = "http:// .";
        Assertions.assertThrows(InvalidURLException.class, () -> UrlValidator.isUrlValid(url));
    }
}

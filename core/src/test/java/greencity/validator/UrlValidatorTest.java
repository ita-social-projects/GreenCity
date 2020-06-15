package greencity.validator;

import static greencity.ModelUtils.getUrl;
import java.net.MalformedURLException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class UrlValidatorTest {

    @Test
    void UrlValidatorTrueTest() throws MalformedURLException {
        String url = getUrl().toString();
        assertTrue(UrlValidator.isUrlValid(url));
    }

    @Test
    void UrlValidatorMalformedURLExceptionFalseTest() {
        String url = "ttt://";
        assertFalse(UrlValidator.isUrlValid(url));
    }

    @Test
    void UrlValidatorURISyntaxExceptionFalseTest() {
        String url = "http:// .";
        assertFalse(UrlValidator.isUrlValid(url));
    }
}

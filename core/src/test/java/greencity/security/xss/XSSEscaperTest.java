package greencity.security.xss;

import org.jsoup.safety.Safelist;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class XSSEscaperTest {

    @Test
    public void xssWithWhitelistTest() {
        String input = "<b onclick=\"alert('XSS')\">Bold text</b><script>alert('XSS')</script>";
        Safelist safelist = new Safelist()
            .addTags("b")
            .addAttributes("b", "onclick");

        String result = XSSEscaper.clean(input, safelist);

        Assertions.assertEquals("<b onclick=\"alert('XSS')\">Bold text</b>", result);
    }

    @Test
    public void saveTextWithWhitelistTest() {
        String input = "<p style=\"color:red\">Paragraph</p>";

        Safelist safelist = new Safelist()
            .addTags("p")
            .addAttributes("p", "style")
            .addProtocols("p", "style", "color:red");

        String result = XSSEscaper.clean(input, safelist);

        Assertions.assertEquals("<p>Paragraph</p>", result);
    }

    @Test
    public void xssWithoutWhitelistTest() {
        String input = "<b onclick=\"alert('XSS')\">text</b><script>alert('XSS')</script>";
        Safelist safelist = Safelist.none();
        String result = XSSEscaper.clean(input, safelist);
        Assertions.assertEquals("text", result);
    }

    @Test
    public void saveTextWithoutWhitelistTest() {
        String input = "<p style=\"color:red\">text</p>";
        Safelist safelist = Safelist.none();
        String result = XSSEscaper.clean(input, safelist);
        Assertions.assertEquals("text", result);
    }
}

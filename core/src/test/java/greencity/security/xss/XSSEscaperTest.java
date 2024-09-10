package greencity.security.xss;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.safety.Safelist;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.ArrayNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

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

    @Test
    public void cleanJsonObjectWithAllowedFields() throws JsonProcessingException {
        String input = "{\"text\":\"<b onclick='alert('XSS')'>text</b><script>alert('XSS')</script>\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(input);

        XSSAllowedElements allowedElements = XSSAllowedElements.builder()
            .safelist(Safelist.basic())
            .fields(List.of("text"))
            .build();

        JsonNode result = XSSEscaper.cleanJson(rootNode, allowedElements);
        String escapedBody = objectMapper.writeValueAsString(result);

        Assertions.assertEquals("{\"text\":\"<b>text</b>\"}", escapedBody);
    }

    @Test
    public void cleanJsonObjectWithNoAllowedFields() throws JsonProcessingException {
        String input = "{\"text\":\"<b onclick='alert('XSS')'>text</b><script>alert('XSS')</script>\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(input);
        XSSAllowedElements allowedElements = XSSAllowedElements.getDefault();
        JsonNode result = XSSEscaper.cleanJson(rootNode, allowedElements);
        String escapedBody = objectMapper.writeValueAsString(result);
        Assertions.assertEquals("{\"text\":\"text\"}", escapedBody);
    }

    @Test
    public void cleanJsonArrayWithMixedNodes() throws JsonProcessingException {
        String input = "[{\"text\":\"<b onclick=\\\"alert('XSS')\\\">Bold text</b>\"}," +
            "{\"innerText\": \"<i>Italic text</i>\"}]";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(input);
        XSSAllowedElements allowedElements = XSSAllowedElements.builder()
            .safelist(Safelist.basic())
            .fields(List.of("field"))
            .build();
        JsonNode result = XSSEscaper.cleanJson(rootNode, allowedElements);
        String escapedBody = objectMapper.writeValueAsString(result);
        Assertions.assertEquals("[{\"text\":\"Bold text\"},{\"innerText\":\"Italic text\"}]", escapedBody);
    }

    @Test
    public void containsHtmlTagsWithHtml() {
        String text = "<div>Some HTML</div>";
        Assertions.assertTrue(XSSEscaper.containsHtmlTags(text));
    }

    @Test
    public void containsHtmlTagsWithoutHtml() {
        String text = "Plain text";
        Assertions.assertFalse(XSSEscaper.containsHtmlTags(text));
    }
}

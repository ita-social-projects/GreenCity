package greencity.security.xss;

import lombok.experimental.UtilityClass;
import org.jsoup.safety.Safelist;
import org.springframework.util.AntPathMatcher;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class stores allowed html tags, attributes, and values.
 *
 * @author Dmytro Dmytruk
 */
@UtilityClass
public class XSSSafelist {
    private static final Map<String, XSSAllowedElements> endpointRules = new HashMap<>();
    private static final XSSAllowedElements defaultAllowedElements = XSSAllowedElements.getDefault();
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final String IFRAME_ATTR = "iframe";
    private static final String CLASS_ATTR = "class";

    static {
        Safelist safelistForEvent = new Safelist()
            .addTags("p", "strong", "em", "u", "span")
            .addAttributes("span", "class");

        XSSAllowedElements allowedElementsForEvent = XSSAllowedElements.builder()
            .safelist(safelistForEvent)
            .fields(List.of("description"))
            .build();

        Safelist safelistForEcoNews = new Safelist()
            .addTags("pre", "p", "span", "a", IFRAME_ATTR, "img")
            .addAttributes("pre", CLASS_ATTR)
            .addAttributes("p", CLASS_ATTR)
            .addAttributes("span", CLASS_ATTR, "style")
            .addAttributes("a", "href", "rel", "target")
            .addAttributes(IFRAME_ATTR, CLASS_ATTR, "src", "frameborder", "allowfullscreen")
            .addAttributes("img", "src", "alt");
        safelistForEcoNews.addEnforcedAttribute(IFRAME_ATTR, "frameborder", "0")
            .addEnforcedAttribute(IFRAME_ATTR, "allowfullscreen", "true")
            .addProtocols("a", "href", "http", "https");

        XSSAllowedElements allowedElementsForEcoNews = XSSAllowedElements.builder()
            .safelist(safelistForEcoNews)
            .fields(List.of("text", "content"))
            .build();

        Safelist safelistForHabit = new Safelist()
            .addTags("p", "strong", "em", "u", "span")
            .addAttributes("span", CLASS_ATTR);

        XSSAllowedElements allowedElementsForHabit = XSSAllowedElements.builder()
            .safelist(safelistForHabit)
            .fields(List.of("description", "descriptionUa"))
            .build();

        endpointRules.put("/events/create", allowedElementsForEvent);
        endpointRules.put("/events/update", allowedElementsForEvent);
        endpointRules.put("/eco-news", allowedElementsForEcoNews);
        endpointRules.put("/eco-news/{id}", allowedElementsForEcoNews);
        endpointRules.put("/habit/custom", allowedElementsForHabit);
        endpointRules.put("/habit/update{id}", allowedElementsForHabit);
    }

    public static XSSAllowedElements getAllowedElementsForEndpoint(String endpoint) {
        for (Map.Entry<String, XSSAllowedElements> entry : endpointRules.entrySet()) {
            if (pathMatcher.match(entry.getKey(), endpoint)) {
                return entry.getValue();
            }
        }
        return defaultAllowedElements;
    }
}

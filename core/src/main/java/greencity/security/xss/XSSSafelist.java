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
    //Html tags
    private static final String P_TAG = "p";
    private static final String A_TAG = "a";
    private static final String U_TAG = "u";
    private static final String EM_TAG = "em";
    private static final String PRE_TAG = "pre";
    private static final String SPAN_TAG = "span";
    private static final String IMG_TAG = "img";
    private static final String STRONG_TAG = "strong";
    //Html attributes
    private static final String IFRAME_ATTR = "iframe";
    private static final String CLASS_ATTR = "class";
    private static final String HREF_ATTR = "href";
    private static final String SRC_ATTR = "src";
    private static final String REL_ATTR = "rel";
    private static final String ALT_ATTR = "rel";
    private static final String STYLE_ATTR = "style";
    private static final String TARGET_ATTR = "target";
    private static final String FRAMEBORDER_ATTR = "frameborder";
    private static final String ALLOWFULLSCREEN_ATTR = "allowfullscreen";

    static {
        Safelist safelistForEvent = new Safelist()
            .addTags(P_TAG, STRONG_TAG, EM_TAG, U_TAG, SPAN_TAG)
            .addAttributes(SPAN_TAG, CLASS_ATTR);

        XSSAllowedElements allowedElementsForEvent = XSSAllowedElements.builder()
            .safelist(safelistForEvent)
            .fields(List.of("description"))
            .build();

        Safelist safelistForEcoNews = new Safelist()
            .addTags(PRE_TAG, P_TAG, SPAN_TAG, A_TAG, IFRAME_ATTR, IMG_TAG)
            .addAttributes(PRE_TAG, CLASS_ATTR)
            .addAttributes(P_TAG, CLASS_ATTR)
            .addAttributes(SPAN_TAG, CLASS_ATTR, STYLE_ATTR)
            .addAttributes(A_TAG, HREF_ATTR, REL_ATTR, TARGET_ATTR)
            .addAttributes(IFRAME_ATTR, CLASS_ATTR, SRC_ATTR, FRAMEBORDER_ATTR, ALLOWFULLSCREEN_ATTR)
            .addAttributes(IMG_TAG, SRC_ATTR, ALT_ATTR);
        safelistForEcoNews.addEnforcedAttribute(IFRAME_ATTR, FRAMEBORDER_ATTR, "0")
            .addEnforcedAttribute(IFRAME_ATTR, ALLOWFULLSCREEN_ATTR, "true")
            .addProtocols(A_TAG, HREF_ATTR, "http", "https");

        XSSAllowedElements allowedElementsForEcoNews = XSSAllowedElements.builder()
            .safelist(safelistForEcoNews)
            .fields(List.of("text", "content"))
            .build();

        Safelist safelistForHabit = new Safelist()
            .addTags(P_TAG, STRONG_TAG, EM_TAG, U_TAG, SPAN_TAG)
            .addAttributes(SPAN_TAG, CLASS_ATTR);

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

package greencity.client.config;

import java.net.URI;
import lombok.experimental.UtilityClass;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@UtilityClass
public class RemoteClientUtils {
    static final String EMAIL_QUERY_PARAMETER = "email";
    static final String PLUS_SYMBOL = "+";
    static final String ENCODED_PLUS_SYMBOL = "%2B";

    static URI encodeEmailParameter(URI uri) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);
        MultiValueMap<String, String> queryParams = builder.build().getQueryParams();
        builder.replaceQuery(null);

        for (var entry : queryParams.entrySet()) {
            String paramKey = entry.getKey();

            if (paramKey.toLowerCase().contains(EMAIL_QUERY_PARAMETER)) {
                for (String value : entry.getValue()) {
                    String encodedValue = value.replace(PLUS_SYMBOL, ENCODED_PLUS_SYMBOL);
                    builder.queryParam(paramKey, encodedValue);
                }
            } else {
                for (String value : entry.getValue()) {
                    builder.queryParam(paramKey, value);
                }
            }
        }

        return builder.build(true).toUri();
    }
}

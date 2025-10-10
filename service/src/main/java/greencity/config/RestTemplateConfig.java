package greencity.config;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class RestTemplateConfig {
    private static final String EMAIL_QUERY_PARAMETER = "email";
    private static final String PLUS_SYMBOL = "+";
    private static final String ENCODED_PLUS_SYMBOL = "%2B";

    /**
     * Creates and configures a RestTemplate bean.
     *
     * @return {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.getInterceptors().add(encodePlusInQueryInterceptor());
        return restTemplate;
    }

    /**
     * Creates and configures a RestClient bean using the provided RestTemplate.
     *
     * @param restTemplate the RestTemplate to be used by the RestClient
     * @return {@link RestClient}
     */
    @Bean(name = "springRestClient")
    public RestClient restClient(RestTemplate restTemplate) {
        return RestClient.create(restTemplate);
    }

    private ClientHttpRequestInterceptor encodePlusInQueryInterceptor() {
        return (request, body, execution) -> {
            HttpRequest modifiedRequest = request;

            URI original = request.getURI();
            String originalQuery = original.getRawQuery();

            if (originalQuery != null
                && originalQuery.contains(PLUS_SYMBOL)
                && originalQuery.toLowerCase().contains(EMAIL_QUERY_PARAMETER)) {
                URI encodedUri = encodeEmailParameter(original);

                if (!encodedUri.equals(original)) {
                    modifiedRequest = new HttpRequest() {
                        @Override
                        public HttpMethod getMethod() {
                            return request.getMethod();
                        }

                        @Override
                        public URI getURI() {
                            return encodedUri;
                        }

                        @Override
                        public HttpHeaders getHeaders() {
                            return request.getHeaders();
                        }
                    };
                }
            }

            return execution.execute(modifiedRequest, body);
        };
    }

    private static URI encodeEmailParameter(URI uri) {
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

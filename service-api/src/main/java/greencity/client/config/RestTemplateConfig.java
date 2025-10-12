package greencity.client.config;

import static greencity.client.config.RemoteClientUtils.EMAIL_QUERY_PARAMETER;
import static greencity.client.config.RemoteClientUtils.PLUS_SYMBOL;
import static greencity.client.config.RemoteClientUtils.encodeEmailParameter;
import java.net.URI;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
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
                        public Map<String, Object> getAttributes() {
                            return request.getAttributes();
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
}

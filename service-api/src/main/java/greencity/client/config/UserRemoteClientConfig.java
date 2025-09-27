package greencity.client.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.ErrorParsingException;
import greencity.exception.exceptions.GreenCityUserServiceException;
import greencity.exception.exceptions.NotFoundException;
import greencity.security.jwt.JwtTool;
import io.netty.channel.ChannelOption;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class UserRemoteClientConfig {
    private static final String EMAIL_QUERY_PARAMETER = "email";
    private static final String PLUS_SYMBOL = "+";
    private static final String ENCODED_PLUS_SYMBOL = "%2B";

    @Value("${greencityuser.server.address}")
    private String greenCityUserBaseUrl;

    @Value("${spring.liquibase.parameters.service-email}")
    private String systemEmail;

    @Value("${webclient.connection-timeout-millis}")
    private Integer connectionTimeoutMillis;

    @Value("${webclient.response-timeout-millis}")
    private Integer responseTimeoutMillis;

    private final JwtTool jwtTool;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl(greenCityUserBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(authorizationHeaderFilter())
            .filter(handlingWebClientExceptions())
            .filter(encodePlusInQuery())
            .clientConnector(
                new ReactorClientHttpConnector(
                    HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis)
                        .responseTimeout(Duration.ofMillis(responseTimeoutMillis))))
            .build();
    }

    private ExchangeFilterFunction authorizationHeaderFilter() {
        List<Role> roles = List.of(Role.ROLE_USER, Role.ROLE_ADMIN);

        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String jwt = jwtTool.createAccessToken(systemEmail, roles);
            String authHeader = AppConstant.TOKEN_PREFIX + jwt;

            ClientRequest authorizedRequest = ClientRequest.from(clientRequest)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .build();

            return Mono.just(authorizedRequest);
        });
    }

    private ExchangeFilterFunction handlingWebClientExceptions() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            HttpStatusCode statusCode = clientResponse.statusCode();
            if (!(statusCode.is4xxClientError() || statusCode.is5xxServerError())) {
                return Mono.just(clientResponse);
            }

            return clientResponse.bodyToMono(String.class)
                .handle((errorBody, sink) -> {
                    switch (clientResponse.statusCode()) {
                        case HttpStatus.NOT_FOUND -> sink.error(new NotFoundException(populateErrorMessage(errorBody)));
                        case HttpStatus.BAD_REQUEST -> sink
                            .error(new BadRequestException(populateErrorMessage(errorBody)));
                        case HttpStatus.INTERNAL_SERVER_ERROR -> sink
                            .error(new GreenCityUserServiceException(populateErrorMessage(errorBody)));
                        default -> sink
                            .error(new IllegalStateException(ErrorMessage.INTERNAL_SERVER_ERROR + errorBody));
                    }
                });
        });
    }

    private String populateErrorMessage(String errorBody) {
        record JsonMessage(String timestamp, short status, String error, String trace, String message, String path) {
        }

        try {
            return new ObjectMapper().readValue(errorBody, JsonMessage.class).message();
        } catch (JsonProcessingException e) {
            throw new ErrorParsingException(e.getMessage());
        }
    }

    private ExchangeFilterFunction encodePlusInQuery() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            URI original = request.url();
            String originalQuery = original.getRawQuery();

            if (originalQuery != null
                && originalQuery.contains(PLUS_SYMBOL)
                && originalQuery.toLowerCase().contains(EMAIL_QUERY_PARAMETER)) {
                URI encodedUri = encodeEmailParameter(original);

                if (!encodedUri.equals(original)) {
                    ClientRequest mutated = ClientRequest.from(request)
                        .url(encodedUri)
                        .build();

                    return Mono.just(mutated);
                }
            }

            return Mono.just(request);
        });
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

        return builder.build().toUri();
    }
}

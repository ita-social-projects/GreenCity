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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
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
    private static final String EMAIL_PARAM_REGEX = "([?&][^&]*[eE]mail[^=&]*=[^&]+)";
    private static final String PLUS_SIGN_IN_EMAIL = "+";
    private static final String ENCODED_PLUS_SIGN = "%2B";

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
                && originalQuery.contains(PLUS_SIGN_IN_EMAIL)
                && originalQuery.matches(EMAIL_PARAM_REGEX)) {
                String encodedQuery = encodeEmailParameter(originalQuery);

                if (encodedQuery.equals(originalQuery)) {
                    URI newUri = UriComponentsBuilder.fromUri(original)
                        .replaceQuery(encodedQuery)
                        .build(true)
                        .toUri();

                    ClientRequest mutated = ClientRequest.from(request)
                        .url(newUri)
                        .build();

                    return Mono.just(mutated);
                }
            }

            return Mono.just(request);
        });
    }

    private static String encodeEmailParameter(String query) {
        Pattern pattern = Pattern.compile(EMAIL_PARAM_REGEX);
        Matcher matcher = pattern.matcher(query);
        StringBuilder encodedQuery = new StringBuilder();

        while (matcher.find()) {
            String queryBeforeEmailParam = matcher.group(1);
            String emailParamKey = matcher.group(2);
            String emailParamValue = matcher.group(3);

            String encodedValue = emailParamValue.replace(PLUS_SIGN_IN_EMAIL, ENCODED_PLUS_SIGN);

            matcher.appendReplacement(encodedQuery,
                Matcher.quoteReplacement(queryBeforeEmailParam + emailParamKey + encodedValue));
        }

        matcher.appendTail(encodedQuery);
        return encodedQuery.toString();
    }
}

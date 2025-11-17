package greencity.client.config;

import static greencity.client.config.RemoteClientUtils.EMAIL_QUERY_PARAMETER;
import static greencity.client.config.RemoteClientUtils.PLUS_SYMBOL;
import static greencity.client.config.RemoteClientUtils.encodeEmailParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.ErrorParsingException;
import greencity.exception.exceptions.GreenCityUserServiceException;
import greencity.exception.exceptions.NotFoundException;
import greencity.properties.RemoteWebClientProperties;
import greencity.security.jwt.JwtTool;
import io.netty.channel.ChannelOption;
import java.net.URI;
import lombok.RequiredArgsConstructor;
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
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class UserRemoteClientConfig {
    private final RemoteWebClientProperties remoteWebClientProperties;

    private final JwtTool jwtTool;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl(remoteWebClientProperties.getGreencityUserServerAddress())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(authorizationHeaderFilter())
            .filter(handlingWebClientExceptions())
            .filter(encodePlusInQuery())
            .clientConnector(
                new ReactorClientHttpConnector(
                    HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, remoteWebClientProperties.getConnectionTimeout())
                        .responseTimeout(Duration.ofMillis(remoteWebClientProperties.getResponseTimeout()))))
            .build();
    }

    private ExchangeFilterFunction authorizationHeaderFilter() {
        List<Role> roles = List.of(Role.ROLE_USER, Role.ROLE_ADMIN);

        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String jwt = jwtTool.createAccessToken(remoteWebClientProperties.getSystemEmailAddress(), roles);
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
}

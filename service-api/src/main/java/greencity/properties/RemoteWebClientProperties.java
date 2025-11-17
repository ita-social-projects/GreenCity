package greencity.properties;

import greencity.constant.ErrorMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Сlass for retrieving configuration values from the runtime environment. Used
 * to access dynamic properties. Provides a flexible alternative to @Value,
 * always getting the latest values without having to restart the application or
 * use /actuator/refresh.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class RemoteWebClientProperties {
    private final Environment environment;

    @PostConstruct
    public void validateProperties() {
        getGreencityServerAddress();
        getGreencityUbsServerAddress();
        getGreencityUserServerAddress();
        getClientAddress();
        getConnectionTimeout();
        getResponseTimeout();
        getSystemEmailAddress();
        log.info("All remoteWebClient properties validated successfully.");
    }

    public String getGreencityServerAddress() {
        String greencityServerAddress = environment.getProperty("address");
        if (!StringUtils.hasText(greencityServerAddress)) {
            log.error(ErrorMessage.GREENCITY_SERVER_ADDRESS_NOT_SET);
            throw new IllegalStateException(ErrorMessage.GREENCITY_SERVER_ADDRESS_NOT_SET);
        }
        return greencityServerAddress;
    }

    public String getGreencityUbsServerAddress() {
        String greencityUbsServerAddress = environment.getProperty("greencityubs.server.address");
        if (!StringUtils.hasText(greencityUbsServerAddress)) {
            log.error(ErrorMessage.GREENCITY_UBS_SERVER_ADDRESS_NOT_SET);
            throw new IllegalStateException(ErrorMessage.GREENCITY_UBS_SERVER_ADDRESS_NOT_SET);
        }
        return greencityUbsServerAddress;
    }

    public String getGreencityUserServerAddress() {
        String greencityUserServerAddress = environment.getProperty("greencityuser.server.address");
        if (!StringUtils.hasText(greencityUserServerAddress)) {
            log.error(ErrorMessage.GREENCITY_USER_SERVICE_ADDRESS_NOT_SET);
            throw new IllegalStateException(ErrorMessage.GREENCITY_USER_SERVICE_ADDRESS_NOT_SET);
        }
        return greencityUserServerAddress;
    }

    public String getClientAddress() {
        String clientAddress = environment.getProperty("client.address");
        if (!StringUtils.hasText(clientAddress)) {
            log.error(ErrorMessage.CLIENT_ADDRESS_NOT_SET);
            throw new IllegalStateException(ErrorMessage.CLIENT_ADDRESS_NOT_SET);
        }
        return clientAddress;
    }

    public Integer getConnectionTimeout() {
        Integer connectionTimeout = environment.getProperty("webclient.connection-timeout-millis", Integer.class);
        if (connectionTimeout == null) {
            log.error(ErrorMessage.WEBCLIENT_CONNECTION_TIMEOUT_NOT_SET);
            throw new IllegalStateException(ErrorMessage.WEBCLIENT_CONNECTION_TIMEOUT_NOT_SET);
        }
        return connectionTimeout;
    }

    public Integer getResponseTimeout() {
        Integer responseTimeout = environment.getProperty("webclient.response-timeout-millis", Integer.class);
        if (responseTimeout == null) {
            log.error(ErrorMessage.WEBCLIENT_RESPONSE_TIMEOUT_NOT_SET);
            throw new IllegalStateException(ErrorMessage.WEBCLIENT_RESPONSE_TIMEOUT_NOT_SET);
        }
        return responseTimeout;
    }

    public String getSystemEmailAddress() {
        String systemEmailAddres = environment.getProperty("spring.liquibase.parameters.service-email");
        if (!StringUtils.hasText(systemEmailAddres)) {
            log.error(ErrorMessage.SYSTEM_EMAIL_ADDRESS_NOT_SET);
            throw new IllegalStateException(ErrorMessage.SYSTEM_EMAIL_ADDRESS_NOT_SET);
        }
        return systemEmailAddres;
    }

}

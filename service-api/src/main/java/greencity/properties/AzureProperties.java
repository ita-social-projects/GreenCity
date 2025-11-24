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
public class AzureProperties {
    private final Environment environment;

    @PostConstruct
    public void validateProperties() {
        getAzureConnectionString();
        getAzureContainerName();
        log.info("All azure properties validated successfully.");
    }

    public String getAzureConnectionString() {
        String connectionString = environment.getProperty("azure.connection.string");
        if (!StringUtils.hasText(connectionString)) {
            log.error(ErrorMessage.AZURE_CONNECTION_STRING_NOT_SET);
            throw new IllegalStateException(ErrorMessage.AZURE_CONNECTION_STRING_NOT_SET);
        }
        return connectionString;
    }

    public String getAzureContainerName() {
        String containerName = environment.getProperty("azure.container.name");
        if (!StringUtils.hasText(containerName)) {
            log.error(ErrorMessage.AZURE_CONTAINER_NAME_NOT_SET);
            throw new IllegalStateException(ErrorMessage.AZURE_CONTAINER_NAME_NOT_SET);
        }
        return containerName;
    }
}

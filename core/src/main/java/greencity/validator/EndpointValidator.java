package greencity.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EndpointValidator {
    private final List<String> validEndpoints;
    private final List<String> validStatuses;

    private EndpointValidator(@Value("${valid.endpoints}") List<String> validEndpointList,
                              @Value("${valid.statuses}")List<String> validStatuses) {
        validEndpoints = validEndpointList;
        this.validStatuses = validStatuses;
    }

    private boolean isValidEndpoint(String endpointTemplate, String actualUrl) {
        String[] templateParts = endpointTemplate.split("/");
        String[] urlParts = actualUrl.split("/");
        if (templateParts.length != urlParts.length) {
            return false;
        }
        for (int i = 0; i < templateParts.length; i++) {
            String templatePart = templateParts[i];
            String urlPart = urlParts[i];

            if (templatePart.startsWith("{") && templatePart.endsWith("}")) {
                if (!urlPart.matches("\\d+")) {
                    return urlPart.matches("\\d{4}-\\d{2}-\\d{2}");
                }
            } else {
                if (!templatePart.equals(urlPart)) {
                    return false;
                }
            }
        }
        return true;
    }


    private boolean hasExtraCharacters(String url) {
        return !validEndpoints.contains(url);
    }

    public boolean checkUrl(String url) {
        if (hasExtraCharacters(url)) {
            for (String validEndpoint : validEndpoints) {
                if (isValidEndpoint(validEndpoint, url)) {
                    return true;
                }
            }
            return false;
        }

        for (String validEndpoint : validEndpoints) {
            if (isValidEndpoint(validEndpoint, url)) {
                return true;
            }
        }
        return false;
    }
}

package greencity.dto.exportsettings;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentDtoTest {

    @Test
    void createEnvironmentDtoWithGivenValuesTest() {
        Map<String, String> envVars = Map.of(
            "JAVA_HOME", "/usr/lib/jvm/java-11-openjdk",
            "APP_ENV", "production");

        EnvironmentDto dto = new EnvironmentDto(envVars);

        assertThat(dto).isNotNull();
        assertThat(dto.environments()).isNotEmpty();
        assertThat(dto.environments()).containsEntry("JAVA_HOME", "/usr/lib/jvm/java-11-openjdk");
        assertThat(dto.environments()).containsEntry("APP_ENV", "production");
    }

    @Test
    void handleEmptyEnvironmentVariablesTest() {
        Map<String, String> emptyEnvVars = Map.of();

        EnvironmentDto dto = new EnvironmentDto(emptyEnvVars);

        assertThat(dto).isNotNull();
        assertThat(dto.environments()).isEmpty();
    }
}

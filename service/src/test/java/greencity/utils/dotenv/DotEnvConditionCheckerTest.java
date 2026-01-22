package greencity.utils.dotenv;

import greencity.config.dotenv.DotEnvConditionChecker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

class DotEnvConditionCheckerTest {
    private static MockedStatic<Paths> mockedPaths;
    private static MockedStatic<Files> mockedFiles;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field cachedValueField = DotEnvConditionChecker.class.getDeclaredField("cachedValue");
        cachedValueField.setAccessible(true);
        cachedValueField.set(null, null);
        mockedPaths.clearInvocations();
        mockedFiles.clearInvocations();
    }

    @BeforeAll
    public static void init() {
        mockedFiles = Mockito.mockStatic(Files.class);
        mockedPaths = Mockito.mockStatic(Paths.class);
    }

    @AfterAll
    public static void cleanup() {
        mockedPaths.close();
        mockedFiles.close();
    }

    @Test
    public void checkIsEnabledReturnsTrueIfPathIsValid() {
        Path paths = Mockito.mock(Path.class);

        mockedPaths.when(() -> Paths.get(anyString())).thenReturn(paths);
        mockedFiles.when(() -> Files.exists(paths)).thenReturn(true);

        assertTrue(DotEnvConditionChecker.isEnabled());
        mockedPaths.verify(() -> Paths.get(anyString()), times(1));
        mockedFiles.verify(() -> Files.exists(paths), times(1));

    }

    @Test
    public void checkIsEnabledReturnsFalseIfPathNotExistsTest() {
        Path paths = Mockito.mock(Path.class);

        mockedPaths.when(() -> Paths.get(anyString())).thenReturn(paths);
        mockedFiles.when(() -> Files.exists(paths)).thenReturn(false);

        assertFalse(DotEnvConditionChecker.isEnabled());
        mockedPaths.verify(() -> Paths.get(anyString()), times(1));
        mockedFiles.verify(() -> Files.exists(paths), times(1));
    }

    @Test
    public void checkIsFalseIfPathIsProtectedTest() {
        mockedPaths.when(() -> Paths.get(anyString())).thenThrow(SecurityException.class);

        assertFalse(DotEnvConditionChecker.isEnabled());
        mockedPaths.verify(() -> Paths.get(anyString()), times(1));
        mockedFiles.verify(() -> Files.exists(any(Path.class)), times(0));
    }

    @Test
    public void checkIsNoInvocationIfAlreadyCalculatedTest() throws NoSuchFieldException, IllegalAccessException {
        Field cachedValueField = DotEnvConditionChecker.class.getDeclaredField("cachedValue");
        cachedValueField.setAccessible(true);
        cachedValueField.set(null, false);

        assertFalse(DotEnvConditionChecker.isEnabled());
        mockedPaths.verify(() -> Paths.get(anyString()), times(0));
        mockedFiles.verify(() -> Files.exists(any(Path.class)), times(0));
    }
}

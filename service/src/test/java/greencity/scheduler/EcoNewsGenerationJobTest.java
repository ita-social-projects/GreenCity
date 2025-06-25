package greencity.scheduler;

import greencity.service.AIService;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

@ExtendWith(MockitoExtension.class)
class EcoNewsGenerationJobTest {

    @Mock
    private JobExecutionContext jobExecutionContextMock;
    @Mock
    private AIService aiServiceMock;
    @InjectMocks
    private EcoNewsGenerationJob ecoNewsGenerationJob;

    @BeforeEach
    void setUp() throws Exception {
        String[] mockLanguages = {"en", "uk"};
        Field field = EcoNewsGenerationJob.class.getDeclaredField("generatedLanguages");
        field.setAccessible(true);
        field.set(ecoNewsGenerationJob, mockLanguages);
    }

    @Test
    void executeWorksOnlyOncePerClassTest() {
        ecoNewsGenerationJob.execute(jobExecutionContextMock);
        verify(aiServiceMock).generateAndSaveEcoNews("en");
        verify(aiServiceMock).generateAndSaveEcoNews("uk");
        verify(aiServiceMock, never()).generateAndSaveEcoNews("invalid");

        reset(aiServiceMock);

        ecoNewsGenerationJob.execute(jobExecutionContextMock);
        verifyNoInteractions(aiServiceMock);
    }
}

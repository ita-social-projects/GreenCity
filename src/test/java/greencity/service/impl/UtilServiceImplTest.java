package greencity.service.impl;

import static junit.framework.TestCase.assertEquals;

import greencity.GreenCityApplication;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class UtilServiceImplTest {
    @InjectMocks
    private UtilServiceImpl utilService;

    @Test
    public void getIdsFromString() {
        List<Long> expectedIds = Arrays.asList(1L, 2L, 3L);

        assertEquals(expectedIds, utilService.getIdsFromString("1,2,3"));
    }
}
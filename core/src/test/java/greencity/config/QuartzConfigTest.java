package greencity.config;

import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {QuartzConfig.class})
@TestPropertySource(properties = {
    "cron.sendContentToSubscribers=0 0 20 ? * SAT",
    "cron1=0 0 12 ? * MON",
    "cron2=0 0 9 ? * WED",
    "cron3=0 0 6 ? * ?",
    "cron4=0 0 14 ? * FRI",
    "cron5=0 0 8 * * ?"
})
class QuartzConfigTest {
    @Autowired
    private QuartzConfig quartzConfig;
    @Value("${cron.sendContentToSubscribers}")
    private String cronExpression;
    @Value("${cron1}")
    private String cron1;
    @Value("${cron2}")
    private String cron2;
    @Value("${cron3}")
    private String cron3;
    @Value("${cron4}")
    private String cron4;

    private static final String cronExpression1 = "0 0 20 ? * SAT";
    private static final String cronExpression2 = "0 0 12 ? * MON";
    private static final String cronExpression3 = "0 0 9 ? * WED";
    private static final String cronExpression4 = "0 0 6 ? * ?";
    private static final String cronExpression5 = "0 0 14 ? * FRI";
    private static final String invokedMethod = "fixCronExpression";

    private Method getFixMethod() throws NoSuchMethodException {
        Method method = QuartzConfig.class.getDeclaredMethod(invokedMethod, String.class);
        method.setAccessible(true);
        return method;
    }

    @Test
    void testFixCronExpressionWithDayOfMonthAndDayOfWeek() throws Exception {
        Method method = getFixMethod();
        String fixedCron = (String) method.invoke(quartzConfig, cronExpression);
        assertEquals(cronExpression1, fixedCron);
    }

    @Test
    void testReplaceDayOfMonthWithQuestionMark() throws Exception {
        Method method = getFixMethod();
        String fixed = (String) method.invoke(quartzConfig, cron1);
        assertEquals(cronExpression2, fixed);
    }

    @Test
    void testKeepValidCronExpressionUnchanged() throws Exception {
        Method method = getFixMethod();
        String fixed = (String) method.invoke(quartzConfig, cron2);
        assertEquals(cronExpression3, fixed);
    }

    @Test
    void testAlreadyCorrectExpression() throws Exception {
        Method method = getFixMethod();
        String fixed = (String) method.invoke(quartzConfig, cron3);
        assertEquals(cronExpression4, fixed);
    }

    @Test
    void testFixBothDayOfMonthAndWeekPresent() throws Exception {
        Method method = getFixMethod();
        String fixed = (String) method.invoke(quartzConfig, cron4);
        assertEquals(cronExpression5, fixed);
    }
}
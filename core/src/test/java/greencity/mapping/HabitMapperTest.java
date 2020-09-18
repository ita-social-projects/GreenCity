package greencity.mapping;

import greencity.ModelUtils;
import greencity.entity.Habit;
import greencity.entity.User;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import mockit.Mock;
import mockit.MockUp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class HabitMapperTest {
    @InjectMocks
    private HabitMapper habitMapper;

    @Test
    void convertTest() {
        User user = ModelUtils.getUser();
        new MockUp<ZonedDateTime>() {
            @Mock
            ZonedDateTime now() {
                return ZonedDateTime.of(1998, 12, 10, 13, 22, 22, 2232, ZoneId.of("UTC"));
            }
        };
        Habit expected = Habit.builder()
            .createDate(ZonedDateTime.now())
            .statusHabit(true)
            .build();

        assertEquals(expected, habitMapper.convert(user));
    }
}

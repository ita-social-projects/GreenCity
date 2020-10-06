/*
package greencity.mapping;

import greencity.ModelUtils;
import greencity.entity.Habit;
import greencity.entity.User;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
public class HabitMapperTest {
    @InjectMocks
    private HabitMapper habitMapper;

    @Test
    public void convertTest() {
        User user = ModelUtils.getUser();
        Habit expected = Habit.builder()
            .createDate(ZonedDateTime.now())
            .statusHabit(true)
            .build();
        Habit result = habitMapper.convert(user);
        assertEquals(expected.getStatusHabit(), result.getStatusHabit());
        assertTrue(result.getUsers().contains(user));
        assertEquals(expected.getCreateDate().truncatedTo(ChronoUnit.SECONDS),
            result.getCreateDate().truncatedTo(ChronoUnit.SECONDS));
    }
}
*/

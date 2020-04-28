package greencity.mapping;

import greencity.ModelUtils;
import greencity.entity.Habit;
import greencity.entity.User;
import java.time.ZonedDateTime;
import static org.junit.Assert.assertEquals;
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
            .user(user)
            .createDate(ZonedDateTime.now())
            .statusHabit(true)
            .build();

        assertEquals(expected, habitMapper.convert(user));
    }
}

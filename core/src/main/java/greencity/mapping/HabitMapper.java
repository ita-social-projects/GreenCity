package greencity.mapping;

import greencity.entity.Habit;
import greencity.entity.User;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class HabitMapper extends AbstractConverter<User, Habit> {
    /**
     * Convert to habit entity.
     *
     * @param user {@link User} current user.
     * @return {@link Habit}
     */
    @Override
    protected Habit convert(User user) {
        List<User> users = new ArrayList<>();
        users.add(user);
        return Habit.builder()
            .createDate(ZonedDateTime.now())
            .users(users)
            .statusHabit(true)
            .build();
    }
}

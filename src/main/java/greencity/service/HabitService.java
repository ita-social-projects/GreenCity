package greencity.service;

import greencity.entity.Habit;
import java.util.List;

public interface HabitService {

    List<Habit> save(List<Habit> habitList);

    void delete (Long id);
}

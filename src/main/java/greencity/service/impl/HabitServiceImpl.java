package greencity.service.impl;

import greencity.dto.habitstatistic.HabitDto;
import greencity.entity.Habit;
import greencity.repository.HabitRepo;
import greencity.service.HabitService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HabitServiceImpl implements HabitService {

    private HabitRepo habitRepo;

    @Autowired
    public HabitServiceImpl(HabitRepo habitRepo) {
        this.habitRepo = habitRepo;
    }

    @Override
    public List<Habit> save(List<Habit> habitList) {
        return habitRepo.saveAll(habitList);
    }

    @Override
    public void delete(Long id) {
        habitRepo.deleteById(id);
    }

}

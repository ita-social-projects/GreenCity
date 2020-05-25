package greencity.config;

import greencity.entity.FactTranslation;
import greencity.repository.FactTranslationRepo;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

/**
 * Config for scheduling.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Configuration
@EnableScheduling
@AllArgsConstructor
public class ScheduleConfig {
    private final FactTranslationRepo factTranslationRepo;

    /**
     * Once a day randomly chooses new fact of day that has not been fact of day during this iteration.
     * factOfDay == 0 - wasn't fact of day, 1 - is today's fact of day, 2 - already was fact of day.
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void chooseNewFactOfTheDay() {
        Optional<List<FactTranslation>> list = factTranslationRepo.findRandomFact();
        if (list.isPresent()) {
            factTranslationRepo.updateFactOfDayStatus(1, 2);
        } else {
            factTranslationRepo.updateFactOfDayStatus(2, 0);
            factTranslationRepo.updateFactOfDayStatus(1, 2);
            list = factTranslationRepo.findRandomFact();
        }
        factTranslationRepo.updateFactOfDayStatusByHabitfactId(1, list.get().get(0).getHabitFact().getId());
    }
}
